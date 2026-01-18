package com.shop.catalog.controller;

import com.shop.catalog.dto.response.CategoryResponse;
import com.shop.catalog.service.CategoryService;
import com.shop.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

  private final CategoryService categoryService;

  @GetMapping
  public ApiResponse<List<CategoryResponse>> getAll() {
    return ApiResponse.ok(categoryService.getCategories());
  }
}
