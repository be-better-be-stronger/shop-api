package com.shop.order.service;

import java.util.List;

import com.shop.order.dto.CheckoutResponse;
import com.shop.order.dto.OrderResponse;

public interface OrderService {
	CheckoutResponse checkout(String email);
	List<OrderResponse> myOrders(String email);
	
	List<OrderResponse> getAllOrders();
}
