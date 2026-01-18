package com.shop.catalog.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.catalog.dto.response.CategoryResponse;
import com.shop.catalog.repository.CategoryRepository;
import com.shop.catalog.service.CategoryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService{

	private final CategoryRepository categoryRepo;

	 @Override
	  @Transactional(readOnly = true)
	  public List<CategoryResponse> getCategories() {
	    return categoryRepo.findAll().stream()
	        .map(c -> new CategoryResponse(c.getId(), c.getName()))
	        .toList();
	  }

}
