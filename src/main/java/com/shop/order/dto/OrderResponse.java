package com.shop.order.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderResponse {
	private final Integer orderId;
	private final BigDecimal total;
	private final String status;
	private final LocalDateTime orderDate;
	private final List<OrderItemResponse> items;
}