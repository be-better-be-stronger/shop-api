package com.shop.order.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
	private Integer orderId;
	private BigDecimal total;
	private String status;
	private LocalDateTime orderDate;
	private List<OrderItemResponse> items;
}