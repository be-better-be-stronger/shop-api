package com.shop.cart.dto.response;

import java.math.BigDecimal;
import java.util.List;

import lombok.Value;


@Value
public class CartResponse {
	Integer id;
	List<CartItemResponse> items;
	BigDecimal subtotal;
	int totalItems;
	Integer version; // chống race condition	
}