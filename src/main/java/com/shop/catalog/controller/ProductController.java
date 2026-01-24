package com.shop.catalog.controller;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shop.catalog.dto.request.PageProductRequest;
import com.shop.catalog.dto.response.ProductResponse;
import com.shop.catalog.service.ProductService;
import com.shop.common.response.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

  private final ProductService productService;

  @GetMapping
  public ApiResponse<Page<ProductResponse>> getProducts( @Valid @ModelAttribute PageProductRequest req) {
      return ApiResponse.ok(productService.search(req));
  }
  
  @GetMapping("/{id}")
  public ApiResponse<ProductResponse> getDetail(@PathVariable Integer id) {
    return ApiResponse.ok(productService.getById(id));
  }
  
  

}
