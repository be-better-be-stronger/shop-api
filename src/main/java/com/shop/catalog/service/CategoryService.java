package com.shop.catalog.service;

import com.shop.catalog.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {
  List<CategoryResponse> getCategories();
  
}
