package com.shop.catalog.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.catalog.dto.request.UpsertProductRequest;
import com.shop.catalog.dto.response.ProductResponse;
import com.shop.catalog.entity.Product;
import com.shop.catalog.repository.CategoryRepository;
import com.shop.catalog.repository.ProductRepository;
import com.shop.catalog.service.ProductService;
import com.shop.common.exception.ApiException;
import com.shop.common.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {
	
	private final ProductRepository productRepo;
	  private final CategoryRepository categoryRepo;
	
	@Override
	  public Page<ProductResponse> getProducts(int page, int size) {
	    Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

	    return productRepo.findByIsActiveTrue(pageable)
	        .map(p -> new ProductResponse(
	            p.getId(),
	            p.getName(),
	            p.getPrice(),
	            p.getStock(),
	            p.getCategory().getId(),
	            p.getCategory().getName()
	        ));
	  }

	@Override
	public ProductResponse create(UpsertProductRequest req) {
		var c = categoryRepo.findById(req.getCategoryId())
		        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, ErrorCode.ERR_CATEGORY_NOT_FOUND.name()));

		    Product p = new Product();
		    p.setName(req.getName());
		    p.setStock(req.getStock());
		    p.setPrice(req.getPrice());
		    p.setCategory(c);
		    p.setIsActive(req.getIsActive() == null ? true : req.getIsActive());

		    return toResponse(productRepo.save(p));
	}

	@Override
	public ProductResponse update(Integer id, UpsertProductRequest req) {
		var p = productRepo.findById(id)
		        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, ErrorCode.ERR_PRODUCT_NOT_FOUND.name()));

		    var c = categoryRepo.findById(req.getCategoryId())
		        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, ErrorCode.ERR_CATEGORY_NOT_FOUND.name()));

		    p.setName(req.getName());
		    p.setStock(req.getStock());
		    p.setPrice(req.getPrice());
		    p.setCategory(c);
		    if (req.getIsActive() != null) p.setIsActive(req.getIsActive());

		    return toResponse(productRepo.save(p));
	}

	@Override
	public void disable(Integer id) {
		var p = productRepo.findById(id)
		        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, ErrorCode.ERR_PRODUCT_NOT_FOUND.name()));
		    p.setIsActive(false);
	}
	
	private ProductResponse toResponse(Product p) {
		ProductResponse res = new ProductResponse();
	    res.setId(p.getId());
	    res.setName(p.getName());
	    res.setPrice(p.getPrice());
	    res.setStock(p.getStock());
	    res.setCategoryId(p.getCategory().getId());
	    res.setCategoryName(p.getCategory().getName());
	    return res;
	}

}
