package com.shop.order.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemResponse {
	private Integer productId;
	private String productName;
	private BigDecimal price;
	private Integer qty;
	private BigDecimal lineTotal;
}