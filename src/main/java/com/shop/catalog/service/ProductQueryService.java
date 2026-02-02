package com.shop.catalog.service;

import org.springframework.data.domain.Page;

import com.shop.catalog.dto.request.PageProductStatusRequest;
import com.shop.catalog.dto.response.ProductResponse;

public interface ProductQueryService {

	Page<ProductResponse> findAdminProducts(PageProductStatusRequest req);

}
