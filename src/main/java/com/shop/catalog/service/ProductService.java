package com.shop.catalog.service;

import org.springframework.data.domain.Page;

import com.shop.catalog.dto.request.PageProductRequest;
import com.shop.catalog.dto.request.UpsertProductRequest;
import com.shop.catalog.dto.response.ProductResponse;

public interface ProductService {
	Page<ProductResponse> search(PageProductRequest pageRequest);

	ProductResponse create(UpsertProductRequest req);
	ProductResponse update(Integer id, UpsertProductRequest req);
	
	void disable(Integer id);

	ProductResponse getById(Integer id);

	ProductResponse updateImageUrl(Integer id, String imageUrl);
}
