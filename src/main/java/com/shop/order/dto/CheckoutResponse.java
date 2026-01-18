package com.shop.order.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CheckoutResponse {
	private Integer orderId;
	private BigDecimal total;
	private String status;
}