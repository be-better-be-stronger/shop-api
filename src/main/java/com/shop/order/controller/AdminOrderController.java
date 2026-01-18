package com.shop.order.controller;

import com.shop.common.response.ApiResponse;
import com.shop.order.service.OrderService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

  private final OrderService orderService;

  @GetMapping
  public ApiResponse<Object> all() {
    return ApiResponse.ok(orderService.getAllOrders());
  }
}


