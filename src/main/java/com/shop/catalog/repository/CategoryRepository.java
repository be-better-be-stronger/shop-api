package com.shop.catalog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shop.catalog.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {}
