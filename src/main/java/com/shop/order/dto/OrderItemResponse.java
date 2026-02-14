package com.shop.order.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class OrderItemResponse {
	Integer productId;
    String productName;
    BigDecimal price;
    Integer qty;
    BigDecimal lineTotal;
}