package com.shop.order.service.impl;

import com.shop.common.exception.ApiException;
import com.shop.order.entity.*;
import com.shop.order.repository.OrderRepository;
import com.shop.order.service.CheckoutTxService;
import com.shop.cart.repository.*;
import com.shop.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CheckoutTxServiceImpl implements CheckoutTxService {

	private final UserRepository userRepo;
	private final CartRepository cartRepo;
	private final CartItemRepository itemRepo;
	private final OrderRepository orderRepo;

	@Override
	@Transactional
	public Object checkoutOnce(String email) {

		var user = userRepo.findByEmail(email)
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));

		var cart = cartRepo.findByUserEmail(email)
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Cart not found"));

		var cartItems = itemRepo.findByCartId(cart.getId());
		if (cartItems.isEmpty()) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Cart is empty");
		}

		Order order = new Order();
		order.setUser(user);

		for (var ci : cartItems) {
			var p = ci.getProduct();
			
			if (Boolean.FALSE.equals(p.getIsActive())) {
				throw new ApiException(HttpStatus.BAD_REQUEST, "Product inactive: " + p.getId());
			}

			if (p.getStock() < ci.getQty()) {
				throw new ApiException(HttpStatus.BAD_REQUEST, "Not enough stock for product " + p.getId());
			}

			p.setStock(p.getStock() - ci.getQty()); // @Version OCC

			OrderItem oi = new OrderItem();
			oi.setProduct(p);
			oi.setQty(ci.getQty());
			oi.setUnitPrice(ci.getUnitPrice());
			
			order.addItem(oi); //  helper set quan hệ 2 chiều
		}
		
		order.setTotal(order.computeTotal()); //  total derived từ items

		orderRepo.save(order);

		itemRepo.deleteAll(cartItems); // clear cart

		Map<String, Object> res = new HashMap<>();
		res.put("orderId", order.getId());
		res.put("total", order.getTotal());
		res.put("status", order.getStatus());
		return res;
	}
}
