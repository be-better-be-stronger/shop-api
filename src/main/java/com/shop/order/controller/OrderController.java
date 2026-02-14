package com.shop.order.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shop.common.response.ApiResponse;
import com.shop.order.dto.CheckoutResponse;
import com.shop.order.dto.OrderResponse;
import com.shop.order.service.OrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

  private final OrderService orderService;

  @PostMapping("/checkout")
  public ApiResponse<CheckoutResponse> checkout(Authentication auth) {
    return ApiResponse.ok(orderService.checkout(auth.getName()));
  }

  @GetMapping
  public ApiResponse<List<OrderResponse>> myOrders(Authentication auth) {
    return ApiResponse.ok(orderService.myOrders(auth.getName()));
  }
}

