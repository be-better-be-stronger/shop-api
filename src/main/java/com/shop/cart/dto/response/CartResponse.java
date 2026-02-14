package com.shop.cart.dto.response;

import java.math.BigDecimal;
import java.util.List;

import lombok.Getter;
import lombok.Value;

@Getter
@Value
public class CartResponse {
	Integer cartId;
	List<CartItemResponse> items;
	BigDecimal total;
}