package com.shop.catalog.repository;

import com.shop.catalog.entity.Product;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {
	Page<Product> findByIsActiveTrue(Pageable pageable);
}
