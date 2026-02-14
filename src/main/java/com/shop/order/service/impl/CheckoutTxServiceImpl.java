package com.shop.order.service.impl;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.cart.repository.CartItemRepository;
import com.shop.cart.repository.CartRepository;
import com.shop.common.ErrorCode;
import com.shop.common.exception.ApiException;
import com.shop.order.dto.CheckoutResponse;
import com.shop.order.entity.Order;
import com.shop.order.entity.OrderItem;
import com.shop.order.repository.OrderRepository;
import com.shop.order.service.CheckoutTxService;
import com.shop.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CheckoutTxServiceImpl implements CheckoutTxService {

	private final UserRepository userRepo;
	private final CartRepository cartRepo;
	private final CartItemRepository itemRepo;
	private final OrderRepository orderRepo;
	@Override
	@Transactional
	public CheckoutResponse checkoutOnce(String email) {

		var user = userRepo.findByEmail(email)
				.orElseThrow(() -> new ApiException(ErrorCode.ERR_NOT_FOUND));

		var cart = cartRepo.findByUserEmail(email)
				.orElseThrow(() -> new ApiException(ErrorCode.ERR_NOT_FOUND));

		var cartItems = itemRepo.findByCartId(cart.getId());
		if (cartItems.isEmpty()) {
			throw new ApiException(ErrorCode.ERR_CART_EMPTY);
		}

		Order order = new Order();
		order.setUser(user);

		for (var ci : cartItems) {
			var p = ci.getProduct();
			
			if (Boolean.FALSE.equals(p.getIsActive())) {
				throw new ApiException(ErrorCode.ERR_NOT_FOUND);
			}

			if (p.getStock() < ci.getQty()) {
				throw new ApiException(ErrorCode.ERR_OUT_OF_STOCK);
			}

			p.setStock(p.getStock() - ci.getQty()); // @Version OCC

			OrderItem oi = new OrderItem();
			oi.setProduct(p);
			oi.setQty(ci.getQty());
			oi.setUnitPrice(ci.getUnitPrice());
			
			order.addItem(oi); //  helper set quan hệ 2 chiều
		}
		BigDecimal total = order.computeTotal();
		order.setTotal(total); //  total derived từ items

		orderRepo.saveAndFlush(order);

		itemRepo.deleteAll(cartItems); // clear cart
		
		return CheckoutResponse.builder()
				.orderId(order.getId())
				.total(total)
				.status(order.getStatus().name())
				.build();
	}
}


