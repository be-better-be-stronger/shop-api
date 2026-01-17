package com.shop.catalog.controller;

import com.shop.catalog.entity.Category;
import com.shop.catalog.repository.CategoryRepository;
import com.shop.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

  private final CategoryRepository categoryRepo;

  @GetMapping
  public ApiResponse<List<Category>> getAll() {
    return ApiResponse.ok(categoryRepo.findAll());
  }
}
