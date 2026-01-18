package com.shop.catalog.service;

import org.springframework.data.domain.Page;

import com.shop.catalog.dto.request.UpsertProductRequest;
import com.shop.catalog.dto.response.ProductResponse;

public interface ProductService {
	Page<ProductResponse> getProducts(int page, int size);

	ProductResponse create(UpsertProductRequest req);
	ProductResponse update(Integer id, UpsertProductRequest req);
	void disable(Integer id);
}
