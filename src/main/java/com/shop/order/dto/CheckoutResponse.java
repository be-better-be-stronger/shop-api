package com.shop.order.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CheckoutResponse {
	Integer orderId;
	BigDecimal total;
	String status;
}