package com.shop.order.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CheckoutResponse {
	private final Integer orderId;
	private final BigDecimal total;
	private final String status;
}