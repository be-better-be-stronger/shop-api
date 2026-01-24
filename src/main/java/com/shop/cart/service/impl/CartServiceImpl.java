package com.shop.cart.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.cart.dto.request.AddCartItemRequest;
import com.shop.cart.dto.request.UpdateCartItemQtyRequest;
import com.shop.cart.dto.response.CartItemResponse;
import com.shop.cart.dto.response.CartResponse;
import com.shop.cart.entity.CartItem;
import com.shop.cart.repository.CartItemRepository;
import com.shop.cart.repository.CartRepository;
import com.shop.cart.service.CartService;
import com.shop.catalog.repository.ProductRepository;
import com.shop.common.ErrorCode;
import com.shop.common.exception.ApiException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

	private final CartRepository cartRepo;
	private final CartItemRepository itemRepo;
	private final ProductRepository productRepo;

	@Override
	public CartResponse getMyCart(String email) {
		var cart = cartRepo.findByUserEmail(email)
				.orElseThrow(() -> new ApiException(ErrorCode.ERR_NOT_FOUND, "Cart is not found"));

		var items = itemRepo.findByCartId(cart.getId());

		BigDecimal total = BigDecimal.ZERO;
		var resItems = new ArrayList<CartItemResponse>();

		for (var i : items) {
			BigDecimal lineTotal = i.computeLineTotal();
			total = total.add(lineTotal);			
			CartItemResponse item = new CartItemResponse(
					i.getId(), 
					i.getProduct().getId(),
					i.getProduct().getName(), 
					i.getUnitPrice(), 
					i.getQty(), 
					lineTotal);
			
			resItems.add(item);
		}

		return new CartResponse(cart.getId(), resItems, total);
	}

	@Override
	@Transactional
	public void addItem(String email, AddCartItemRequest req) {
		var cart = cartRepo.findByUserEmail(email)
				.orElseThrow(() -> new ApiException(ErrorCode.ERR_NOT_FOUND, "Cart is not found"));

		var p = productRepo.findById(req.getProductId())
				.orElseThrow(() -> new ApiException(ErrorCode.ERR_NOT_FOUND));		
		
		if (Boolean.FALSE.equals(p.getIsActive())) {
			throw new ApiException(ErrorCode.ERR_BAD_REQUEST, "Product is inactive");
		}
		
		if (req.getQty() > p.getStock())
			throw new ApiException(ErrorCode.ERR_BAD_REQUEST, "Not enough stock");

		var existing = itemRepo.findByCartIdAndProductId(cart.getId(), p.getId());
		
		if (existing.isPresent()) {
			var item = existing.get();
			int totalQty = item.getQty() + req.getQty();
			log.debug("totalQty={}, stock={}", totalQty, p.getStock());
			if(totalQty > p.getStock()) 
				throw new ApiException(ErrorCode.ERR_BAD_REQUEST, "Not enough stock");
			item.setQty(totalQty);
			itemRepo.save(item);
			return;
		}

		CartItem item = new CartItem();
		item.setCart(cart);
		item.setProduct(p);
		item.setQty(req.getQty());
		item.setUnitPrice(p.getPrice()); // snapshot
		itemRepo.save(item);
	}

	@Override
	@Transactional
	public void updateQty(String email, Integer itemId, UpdateCartItemQtyRequest req) {
		var cart = cartRepo.findByUserEmail(email)
				.orElseThrow(() -> new ApiException(ErrorCode.ERR_NOT_FOUND));

		var item = itemRepo.findById(itemId)
				.orElseThrow(() -> new ApiException(ErrorCode.ERR_NOT_FOUND));

		if (!item.getCart().getId().equals(cart.getId())) {
			throw new ApiException(ErrorCode.ERR_FORBIDDEN, "It's not your cart");
		}

		item.setQty(req.getQty());
	}

	@Override
	@Transactional
	public void removeItem(String email, Integer itemId) {
		var cart = cartRepo.findByUserEmail(email)
				.orElseThrow(() -> new ApiException(ErrorCode.ERR_NOT_FOUND));

		var item = itemRepo.findById(itemId)
				.orElseThrow(() -> new ApiException(ErrorCode.ERR_NOT_FOUND));

		if (!item.getCart().getId().equals(cart.getId())) {
			throw new ApiException(ErrorCode.ERR_FORBIDDEN, "It's not your cart");
		}

		itemRepo.delete(item);
	}
}
