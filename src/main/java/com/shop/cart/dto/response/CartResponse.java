package com.shop.cart.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartResponse {
	private Integer cartId;
	private List<CartItemResponse> items;
	private BigDecimal total;
}