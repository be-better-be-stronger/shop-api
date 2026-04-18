package com.shop.cart.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.shop.cart.entity.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
	List<CartItem> findByCartId(Integer cartId);

	Optional<CartItem> findByCartIdAndProductId(Integer cartId, Integer productId);

	@Query("""
			    SELECT ci FROM CartItem ci
			    JOIN FETCH ci.product
			    WHERE ci.cart.id = :cartId
			""")
	List<CartItem> findByCartIdWithProduct(Integer cartId);
}
