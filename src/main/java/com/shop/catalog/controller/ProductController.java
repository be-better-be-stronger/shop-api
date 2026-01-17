package com.shop.catalog.controller;

import com.shop.catalog.entity.Product;
import com.shop.catalog.repository.ProductRepository;
import com.shop.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

  private final ProductRepository productRepo;

  @GetMapping
  public ApiResponse<Page<Product>> getProducts(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
    return ApiResponse.ok(productRepo.findByIsActiveTrue(pageable));
  }
}
