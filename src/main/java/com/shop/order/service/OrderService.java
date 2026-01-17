package com.shop.order.service;

public interface OrderService {
  Object checkout(String email);
  Object myOrders(String email);
}
