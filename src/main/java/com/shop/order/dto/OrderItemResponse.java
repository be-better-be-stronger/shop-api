package com.shop.order.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderItemResponse {
	private final Integer productId;
    private final String productName;
    private final BigDecimal price;
    private final Integer qty;
    private final BigDecimal lineTotal;
}