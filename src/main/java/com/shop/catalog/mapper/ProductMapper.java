package com.shop.catalog.mapper;

import com.shop.catalog.dto.response.ProductResponse;
import com.shop.catalog.entity.Product;

public class ProductMapper {

    public static ProductResponse toResponse(Product p) {
        if (p == null) return null;

        return ProductResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .stock(p.getStock())
                .price(p.getPrice())
                .categoryId(p.getCategory().getId())
                .categoryName(p.getCategory().getName())
                .isActive(p.getIsActive())
                .imageUrl(p.getImageUrl())
                .build();
    }
}
