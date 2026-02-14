package com.shop.order.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class OrderResponse {
	Integer orderId;
	BigDecimal total;
	String status;
	LocalDateTime orderDate;
	List<OrderItemResponse> items;
}