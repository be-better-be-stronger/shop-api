package com.shop.order.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.cart.repository.CartRepository;
import com.shop.catalog.entity.Product;
import com.shop.catalog.repository.ProductRepository;
import com.shop.common.ErrorCode;
import com.shop.common.exception.ApiException;
import com.shop.order.dto.CheckoutResponse;
import com.shop.order.entity.Order;
import com.shop.order.entity.OrderItem;
import com.shop.order.repository.OrderRepository;
import com.shop.order.service.CheckoutTxService;
import com.shop.user.entity.User;
import com.shop.user.entity.UserStatus;
import com.shop.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CheckoutTxServiceImpl implements CheckoutTxService {

	private final UserRepository userRepo;
	private final CartRepository cartRepo;
	private final OrderRepository orderRepo;
	private final ProductRepository productRepo;

	@Override
	@Transactional
	public CheckoutResponse checkoutOnce(String email) {

		User user = userRepo.findByEmail(email)
				.orElseThrow(() -> new ApiException(ErrorCode.ERR_NOT_FOUND, "User is not found"));
		
		if (user.getStatus() != UserStatus.ACTIVE) {
		    throw new ApiException(ErrorCode.ERR_FORBIDDEN, "User is disabled");
		}

		var cart = cartRepo.findByUserEmailWithItems(email).orElseThrow(() -> new ApiException(ErrorCode.ERR_NOT_FOUND,
				String.format("Cart is not found by user email=%s", email)));

		var cartItems = cart.getItems();

		if (cartItems.isEmpty()) {
			throw new ApiException(ErrorCode.ERR_CART_EMPTY, "Cart is empty");
		}

		Order order = new Order();
		order.setUser(user);

		List<Integer> ids = cartItems.stream().map(ci -> ci.getProduct().getId()).toList();

		Map<Integer, Product> productMap = productRepo.findAllById(ids).stream()
				.collect(Collectors.toMap(Product::getId, p -> p));

		for (var ci : cartItems) {
			var p = productMap.get(ci.getProduct().getId());
			
			if (p == null) 
		        throw new ApiException(ErrorCode.ERR_NOT_FOUND, "Product is not found");
		    

			if (Boolean.FALSE.equals(p.getIsActive())) 
				throw new ApiException(ErrorCode.ERR_BAD_REQUEST, "Product is inactive");
			

			if (p.getStock() < ci.getQty()) 
				throw new ApiException(ErrorCode.ERR_OUT_OF_STOCK, "Out of stock");
			

			p.setStock(p.getStock() - ci.getQty()); // @Version OCC

			OrderItem oi = new OrderItem();
			oi.setProduct(p);
			oi.setQty(ci.getQty());
			oi.setUnitPrice(ci.getUnitPrice());

			order.addItem(oi); // helper set quan hệ 2 chiều
		}
		BigDecimal total = order.computeTotal();
		order.setTotal(total); // total derived từ items

		orderRepo.save(order);

		cart.clear(); // clear cart

		return CheckoutResponse.builder().orderId(order.getId()).total(total).status(order.getStatus().name()).build();
	}
}
