package com.shop.cart.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shop.cart.entity.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
	List<CartItem> findByCartId(Integer cartId);
	Optional<CartItem> findByCartIdAndProductId(Integer cartId, Integer productId);
}
