package com.shop.order.controller;

import com.shop.common.response.ApiResponse;
import com.shop.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

  private final OrderService orderService;

  @PostMapping("/checkout")
  public ApiResponse<Object> checkout(Authentication auth) {
    return ApiResponse.ok(orderService.checkout(auth.getName()));
  }

  @GetMapping
  public ApiResponse<Object> myOrders(Authentication auth) {
    return ApiResponse.ok(orderService.myOrders(auth.getName()));
  }
}
