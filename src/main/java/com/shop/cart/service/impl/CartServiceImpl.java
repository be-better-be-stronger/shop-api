package com.shop.cart.service.impl;

import com.shop.cart.dto.request.AddCartItemRequest;
import com.shop.cart.dto.request.UpdateCartItemQtyRequest;
import com.shop.cart.dto.response.CartItemResponse;
import com.shop.cart.dto.response.CartResponse;
import com.shop.cart.entity.CartItem;
import com.shop.cart.repository.*;
import com.shop.cart.service.CartService;
import com.shop.catalog.repository.ProductRepository;
import com.shop.common.exception.ApiException;
import com.shop.common.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;

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
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, ErrorCode.ERR_CART_NOT_FOUND.name()));

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
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, ErrorCode.ERR_CART_NOT_FOUND.name()));

		var p = productRepo.findById(req.getProductId())
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, ErrorCode.ERR_PRODUCT_NOT_FOUND.name()));		
		
		if (Boolean.FALSE.equals(p.getIsActive())) {
			throw new ApiException(HttpStatus.BAD_REQUEST, ErrorCode.ERR_PRODUCT_INACTIVE.name());
		}
		
		if (req.getQty() > p.getStock())
			throw new ApiException(HttpStatus.BAD_REQUEST, ErrorCode.ERR_NOT_ENOUGH_STOCK.name());

		var existing = itemRepo.findByCartIdAndProductId(cart.getId(), p.getId());
		
		if (existing.isPresent()) {
			var item = existing.get();
			int totalQty = item.getQty() + req.getQty();
			log.debug("totalQty={}, stock={}", totalQty, p.getStock());
			if(totalQty > p.getStock()) 
				throw new ApiException(HttpStatus.BAD_REQUEST, ErrorCode.ERR_NOT_ENOUGH_STOCK.name());
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
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, ErrorCode.ERR_CART_NOT_FOUND.name()));

		var item = itemRepo.findById(itemId)
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, ErrorCode.ERR_CART_ITEM_NOT_FOUND.name()));

		if (!item.getCart().getId().equals(cart.getId())) {
			throw new ApiException(HttpStatus.FORBIDDEN, ErrorCode.ERR_NOT_YOUR_CART_ITEM.name());
		}

		item.setQty(req.getQty());
	}

	@Override
	@Transactional
	public void removeItem(String email, Integer itemId) {
		var cart = cartRepo.findByUserEmail(email)
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, ErrorCode.ERR_CART_NOT_FOUND.name()));

		var item = itemRepo.findById(itemId)
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, ErrorCode.ERR_CART_ITEM_NOT_FOUND.name()));

		if (!item.getCart().getId().equals(cart.getId())) {
			throw new ApiException(HttpStatus.FORBIDDEN,  ErrorCode.ERR_NOT_YOUR_CART_ITEM.name());
		}

		itemRepo.delete(item);
	}
}
