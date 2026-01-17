package com.shop.cart.entity;

import java.util.ArrayList;
import java.util.List;

import com.shop.user.entity.User;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "carts")
@Getter @Setter
public class Cart {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", unique = true, nullable = false)
	private User user;

	@OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CartItem> items = new ArrayList<>();

//	public void addOrInc(Product p, int qty) {
//		if (qty <= 0)
//			return;
//		CartItem it = items.stream().filter(x -> x.getProduct().getId().equals(p.getId())).findFirst().orElse(null);
//
//		if (it == null) {
//			it = new CartItem(this, p, qty, p.getPrice());
//			items.add(it); // inverse list
//		} else {
//			it.setQty(it.getQty() + qty); // dirty checking
//		}
//	}
//
//	public void removeProduct(Integer productId) {
//		items.removeIf(x -> x.getProduct().getId().equals(productId));
//		// orphanRemoval => DELETE cart_items
//	}
//
//	public int getQtyOf(int productId) {
//		 return items.stream()
//			        .filter(i -> i.getProduct().getId() == productId)
//			        .mapToInt(CartItem::getQty)
//			        .findFirst()
//			        .orElse(0);
//	}
//	
//	public CartItem findItem(Integer productId) {
//		for(CartItem it : items) {
//			if(it.getProduct().getId().equals(productId)) return it;
//		}
//		return null;
//	}
}
