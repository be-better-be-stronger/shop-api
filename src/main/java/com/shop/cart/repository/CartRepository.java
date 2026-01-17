package com.shop.cart.repository;

import com.shop.cart.entity.Cart;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Integer> {
	Optional<Cart> findByUserEmail(String email);
	
}
