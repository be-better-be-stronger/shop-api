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
import com.shop.catalog.repository.CategoryRepository;
import com.shop.catalog.repository.ProductRepository;
import com.shop.catalog.service.ProductService;
import com.shop.common.ErrorCode;
import com.shop.common.exception.ApiException;
import com.shop.common.upload.LocalUploadService;
import com.shop.common.upload.Tx;
import com.shop.common.upload.UploadDir;

import lombok.RequiredArgsConstructor;

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
		if (size < 1) size = 10;
		if (size > 50) size = 50;

		// 3) sort field default + whitelist
		String sortField = normalize(req.getSort());
		if (sortField == null || sortField.isBlank())
			sortField = "name";
		if (!ALLOWED_SORTS.contains(sortField))
			sortField = "name";

		// 4) direction default + whitelist (chống null/bậy)
		String dir = normalize(req.getDir());
		if (dir == null || dir.isBlank()) dir = "asc";
		if (!ALLOWED_DIRS.contains(dir)) dir = "asc";

		Sort.Direction direction = "desc".equals(dir) ? Sort.Direction.DESC : Sort.Direction.ASC;
		Pageable pageable = PageRequest.of(pageIndex, size, Sort.by(direction, sortField));

		// 5) filter q/cat (optional)
		String q = normalize(req.getQ());
		Integer cat = req.getCat();

		// NHÁNH A: không filter
		if ((q == null || q.isBlank()) && cat == null) {
			return productRepo.findByIsActiveTrue(pageable).map(this::toResponse);
		}

		// NHÁNH B: có q/cat
		return productRepo.searchActive(q, cat, pageable).map(this::toResponse);
	}

	@Override
	public ProductResponse getById(Integer id) {
		return productRepo.findById(id).filter(p -> Boolean.TRUE.equals(p.getIsActive())).map(this::toResponse)
				.orElseThrow(() -> new ApiException(ErrorCode.ERR_NOT_FOUND));
	}

	@Override
	public ProductResponse create(UpsertProductRequest req) {

		// imageUrl frontend gửi lên: staging url (hoặc null)
		String stagingUrl = normalize(req.getImageUrl());
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

		return toResponse(p);
	}

	@Override
	public ProductResponse update(Integer id, UpsertProductRequest req) {
		Product p = productRepo.findById(id).orElseThrow(() -> new ApiException(ErrorCode.ERR_NOT_FOUND));

		Category c = categoryRepo.findById(req.getCategoryId())
				.orElseThrow(() -> new ApiException(ErrorCode.ERR_NOT_FOUND));

		p.setName(req.getName());
		p.setStock(req.getStock());
		p.setPrice(req.getPrice());
		p.setCategory(c);
		if (req.getIsActive() != null)
			p.setIsActive(req.getIsActive());
		p.setDescription(req.getDescription());

		String old = p.getImageUrl();
		String incoming = normalize(old);

		boolean changedImage = incoming != null && !incoming.equals(old);

		if (changedImage)
			p.setImageUrl(incoming);

		try {
			Product saved = productRepo.save(p);
			// chỉ xóa file cũ sau commit nếu ảnh đổi thật
			if (changedImage && old != null)
				Tx.afterCommit(() -> uploadService.deleteByUrl(old));
			return toResponse(saved);
		} catch (RuntimeException ex) {
			// update fail -> rollback file mới để khỏi rác
			if (changedImage && incoming != null)
				uploadService.deleteByUrl(incoming);
			throw ex;
		}
	}

	@Override
	public void disable(Integer id) {
		var p = productRepo.findById(id).orElseThrow(() -> new ApiException(ErrorCode.ERR_NOT_FOUND));
		p.setIsActive(false);
	}
	
	@Override
	public ProductResponse updateImageUrl(Integer id, String imageUrl) {
		Product p = productRepo.findById(id).orElseThrow(() -> new ApiException(ErrorCode.ERR_NOT_FOUND));
		p.setImageUrl(imageUrl);
		return toResponse(productRepo.save(p));
	}

	private ProductResponse toResponse(Product p) {
		return ProductResponse.builder().id(p.getId()).name(p.getName()).stock(p.getStock()).price(p.getPrice())
				.categoryId(p.getCategory().getId()).categoryName(p.getCategory().getName()).isActive(p.getIsActive())
				.imageUrl(p.getImageUrl()).build();
	}

	private String normalize(String s) {
		if (s == null)
			return null;
		String t = s.trim();
		return t.isEmpty() ? null : t;
	}
	
	private void safeDelete(String... urls) {
	    if (urls == null) return;

	    for (String url : urls) {
	        try {
	            if (url != null) {
	                uploadService.deleteByUrl(url);
	            }
	        } catch (Exception ignore) { }
	    }
	}
	
	
	

}
