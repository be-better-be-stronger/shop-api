package com.shop.cart.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.shop.cart.entity.Cart;

public interface CartRepository extends JpaRepository<Cart, Integer> {
	Optional<Cart> findByUserEmail(String email);	
	
	@Query("SELECT c FROM Cart c LEFT JOIN FETCH c.items WHERE c.user.email = :email")
	Optional<Cart> findByUserEmailWithItems(String email);
}
