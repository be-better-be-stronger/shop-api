package com.shop.catalog.service.impl;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.catalog.dto.request.PageProductRequest;
import com.shop.catalog.dto.request.UpsertProductRequest;
import com.shop.catalog.dto.response.ProductResponse;
import com.shop.catalog.entity.Category;
import com.shop.catalog.entity.Product;
import com.shop.catalog.mapper.ProductMapper;
import com.shop.catalog.repository.CategoryRepository;
import com.shop.catalog.repository.ProductRepository;
import com.shop.catalog.service.ProductService;
import com.shop.common.ErrorCode;
import com.shop.common.exception.ApiException;
import com.shop.common.upload.LocalUploadService;
import com.shop.common.upload.Tx;
import com.shop.common.upload.UploadDir;
import com.shop.common.util.TextNormalizer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

	private final ProductRepository productRepo;
	private final CategoryRepository categoryRepo;
	private final LocalUploadService uploadService;

	private static final Set<String> ALLOWED_SORTS = Set.of("name", "price");
	private static final Set<String> ALLOWED_DIRS = Set.of("asc", "desc");

	@Override
	public Page<ProductResponse> search(PageProductRequest req) {
		// page: client 1-based -> Spring 0-based
		int pageIndex = Math.max(req.getPage() - 1, 0);

		// 2. claim size
		int size = req.getSize();
		if (size < 1)
			size = 10;
		if (size > 50)
			size = 50;

		// 3) sort field default + whitelist
		String sortField = TextNormalizer.normalize(req.getSort());
		if (sortField == null || sortField.isBlank())
			sortField = "name";
		if (!ALLOWED_SORTS.contains(sortField))
			sortField = "name";

		// 4) direction default + whitelist (chống null/bậy)
		String dir = TextNormalizer.normalize(req.getDir());
		if (dir == null || dir.isBlank())
			dir = "asc";
		if (!ALLOWED_DIRS.contains(dir))
			dir = "asc";

		Sort.Direction direction = "desc".equals(dir) ? Sort.Direction.DESC : Sort.Direction.ASC;
		Pageable pageable = PageRequest.of(pageIndex, size, Sort.by(direction, sortField));

		// 5) filter q/cat (optional)
		String q = TextNormalizer.normalize(req.getQ());
		Integer cat = req.getCat();

		// NHÁNH A: không filter
		if ((q == null || q.isBlank()) && cat == null) {
			return productRepo.findByIsActiveTrue(pageable).map(ProductMapper::toResponse);
		}

		// NHÁNH B: có q/cat
		return productRepo.searchActive(q, cat, pageable).map(ProductMapper::toResponse);
	}
	
	
	@Override
	public ProductResponse getById(Integer id) {
		return productRepo.findById(id).filter(p -> Boolean.TRUE.equals(p.getIsActive())).map(ProductMapper::toResponse)
				.orElseThrow(() -> new ApiException(ErrorCode.ERR_NOT_FOUND));
	}

	@Override
	public ProductResponse create(UpsertProductRequest req) {

		// imageUrl frontend gửi lên: staging url (hoặc null)
		String stagingUrl = TextNormalizer.normalize(req.getImageUrl());
		String finalUrl = null;

		Category category = categoryRepo.findById(req.getCategoryId())
				.orElseThrow(() -> new ApiException(ErrorCode.ERR_NOT_FOUND));

		Product p = new Product();
		try {
			// 1) insert product trước (imageUrl = null để tránh DB trỏ link staging)
			p.setName(req.getName());
			p.setStock(req.getStock());
			p.setPrice(req.getPrice());
			p.setIsActive(req.getIsActive() != null ? req.getIsActive() : true);
			p.setImageUrl(null); // quan trọng
			p.setCategory(category);
			p.setDescription(req.getDescription());

			productRepo.save(p); // insert (có id)

			// 2) nếu có stagingUrl thì move sang products rồi update
			if (stagingUrl != null && stagingUrl.startsWith("/uploads/")) {
				finalUrl = uploadService.moveImage(stagingUrl, UploadDir.PRODUCTS);
				p.setImageUrl(finalUrl); // dirty check
			}
		} catch (Exception e) {
			safeDelete(stagingUrl, finalUrl);
			throw (e instanceof ApiException) ? (ApiException) e : new ApiException(ErrorCode.ERR_SERVER);
		}

		return ProductMapper.toResponse(p);
	}

	@Override
	public ProductResponse update(Integer id, UpsertProductRequest req) {

		log.info("[ProductUpdate] start id={}, categoryId={}, hasImageUrl={}, isActive={}", id, req.getCategoryId(),
				req.getImageUrl() != null, req.getIsActive());

		Product p = productRepo.findById(id).orElseThrow(() -> {
			log.warn("[ProductUpdate] product not found id={}", id);
			return new ApiException(ErrorCode.ERR_NOT_FOUND);
		});

		Category c = categoryRepo.findById(req.getCategoryId()).orElseThrow(() -> {
			log.warn("[ProductUpdate] category not found id={}, categoryId={}", id, req.getCategoryId());
			return new ApiException(ErrorCode.ERR_NOT_FOUND);
		});

		// 1) update normal fields
		p.setName(req.getName());
		p.setStock(req.getStock());
		p.setPrice(req.getPrice());
		p.setCategory(c);
		if (req.getIsActive() != null)
			p.setIsActive(req.getIsActive());
		p.setDescription(req.getDescription());

		// 2) image logic
		String oldUrl = TextNormalizer.normalize(p.getImageUrl());
		String incoming = TextNormalizer.normalize(req.getImageUrl());

		boolean hasIncoming = incoming != null && !incoming.isBlank();
		boolean changedImage = hasIncoming && !incoming.equals(oldUrl);

		log.debug("[ProductUpdate] image check id={}, oldUrl={}, incoming={}, hasIncoming={}, changedImage={}", id,
				oldUrl, incoming, hasIncoming, changedImage);

		String newFinalUrl = null; // để rollback nếu save fail

		if (changedImage) {

			if (!incoming.startsWith("/uploads/")) {
				log.warn("[ProductUpdate] invalid image url prefix id={}, incoming={}", id, incoming);
				throw new ApiException(ErrorCode.ERR_BAD_REQUEST, "IMAGE_URL_INVALID");
			}

			if (incoming.startsWith("/uploads/staging/")) {
				log.info("[ProductUpdate] moving image staging->products id={}, staging={}", id, incoming);

				newFinalUrl = uploadService.moveImage(incoming, UploadDir.PRODUCTS);
				p.setImageUrl(newFinalUrl);

				log.info("[ProductUpdate] moved image OK id={}, finalUrl={}", id, newFinalUrl);

			} else if (incoming.startsWith("/uploads/products/")) {

				p.setImageUrl(incoming);
				log.debug("[ProductUpdate] keep/set products image id={}, url={}", id, incoming);

			} else {
				log.warn("[ProductUpdate] invalid image dir id={}, incoming={}", id, incoming);
				throw new ApiException(ErrorCode.ERR_BAD_REQUEST, "IMAGE_URL_DIR_INVALID");
			}
		}

		try {
			Product saved = productRepo.saveAndFlush(p);
			log.info("[ProductUpdate] db update OK id={}, changedImage={}", id, changedImage);

			// 3) delete old file AFTER COMMIT (chỉ khi đổi ảnh thật)
			if (changedImage && oldUrl != null) {
				log.info("[ProductUpdate] schedule delete old image after commit id={}, oldUrl={}", id, oldUrl);
				Tx.afterCommit(() -> {
					try {
						uploadService.deleteByUrl(oldUrl);
						log.info("[ProductUpdate] deleted old image after commit id={}, oldUrl={}", id, oldUrl);
					} catch (RuntimeException e) {
						// xóa file fail không nên làm fail request vì DB đã commit
						log.error("[ProductUpdate] failed to delete old image after commit id={}, oldUrl={}", id,
								oldUrl, e);
					}
				});
			}

			log.info("[ProductUpdate] success id={}, finalImageUrl={}", id, saved.getImageUrl());
			return ProductMapper.toResponse(saved);

		} catch (RuntimeException ex) {

			log.error("[ProductUpdate] failed id={}, will cleanup newFinalUrl={}", id, newFinalUrl, ex);

			// 4) save fail -> cleanup new file (nếu đã move ra products)
			if (newFinalUrl != null) {
				try {
					uploadService.deleteByUrl(newFinalUrl);
					log.info("[ProductUpdate] cleanup new image OK id={}, newFinalUrl={}", id, newFinalUrl);
				} catch (RuntimeException cleanupEx) {
					log.error("[ProductUpdate] cleanup new image FAILED id={}, newFinalUrl={}", id, newFinalUrl,
							cleanupEx);
				}
			}

			// QUAN TRỌNG: giữ ex gốc để biết lỗi DB gì
			throw ex;
			// Nếu mày muốn luôn trả ERR_SERVER cho client thì:
			// throw new ApiException(ErrorCode.ERR_SERVER, "UPDATE_PRODUCT_FAILED", ex);
		}
	}

	@Override
	public void disable(Integer id) {
		var p = productRepo.findById(id).orElseThrow(() -> new ApiException(ErrorCode.ERR_NOT_FOUND));
		p.setIsActive(false);
	}
	
	@Override
	public void enable(Integer id) {
		var p = productRepo.findById(id).orElseThrow(() -> new ApiException(ErrorCode.ERR_NOT_FOUND));
		p.setIsActive(true);		
	}

	@Override
	public ProductResponse updateImageUrl(Integer id, String imageUrl) {
		Product p = productRepo.findById(id).orElseThrow(() -> new ApiException(ErrorCode.ERR_NOT_FOUND));
		p.setImageUrl(imageUrl);
		return ProductMapper.toResponse(productRepo.save(p));
	}

	private void safeDelete(String... urls) {
		if (urls == null)
			return;

		for (String url : urls) {
			try {
				if (url != null) {
					uploadService.deleteByUrl(url);
				}
			} catch (Exception ignore) {
			}
		}
	}

	


}
