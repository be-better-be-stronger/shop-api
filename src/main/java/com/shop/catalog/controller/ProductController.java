package com.shop.catalog.controller;

import com.shop.catalog.dto.response.ProductResponse;
import com.shop.catalog.service.ProductService;
import com.shop.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

  private final ProductService productService;

  @GetMapping
  public ApiResponse<Page<ProductResponse>> getProducts(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
	  return ApiResponse.ok(productService.getProducts(page, size));
  }
}
