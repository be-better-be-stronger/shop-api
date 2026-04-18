package com.shop.cart.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.cart.dto.request.AddCartItemRequest;
import com.shop.cart.dto.request.UpdateCartItemQtyRequest;
import com.shop.cart.dto.response.CartResponse;
import com.shop.cart.entity.Cart;
import com.shop.cart.entity.CartItem;
import com.shop.cart.mapper.CartMapper;
import com.shop.cart.repository.CartItemRepository;
import com.shop.cart.repository.CartRepository;
import com.shop.cart.service.CartService;
import com.shop.catalog.entity.Product;
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
	private final CartMapper cartMapper;

	@Override
	public CartResponse getMyCart(String email) {
		var cart = findCartByUserEmail(email);

		var items = itemRepo.findByCartIdWithProduct(cart.getId());

		return cartMapper.toCartResponse(cart, items);
	}

	@Override
	@Transactional
	public CartResponse addItem(String email, AddCartItemRequest req, Integer version) {
		var cart = findCartByUserEmail(email);

		if (!cart.getVersion().equals(version)) {
			throw new ApiException(ErrorCode.ERR_CONFLICT, "Cart has been modified");
		}

		var product = productRepo.findById(req.getProductId())
				.orElseThrow(() -> new ApiException(ErrorCode.ERR_NOT_FOUND, "Product is not found"));

		if (Boolean.FALSE.equals(product.getIsActive())) {
			throw new ApiException(ErrorCode.ERR_BAD_REQUEST, "Product is inactive");
		}

		int currentQty = cart.getQtyOfItem(product.getId());
		int totalQty = currentQty + req.getQty();

		if (totalQty > product.getStock()) {
			throw new ApiException(ErrorCode.ERR_BAD_REQUEST, "Not enough stock");
		}

		cart.addProduct(product, req.getQty(), product.getPrice());

		var items = itemRepo.findByCartIdWithProduct(cart.getId());

		return cartMapper.toCartResponse(cart, items);
	}

	@Override
	@Transactional
	public CartResponse updateQty(String email, Integer itemId, UpdateCartItemQtyRequest req, Integer version) {
		var cart = findCartByUserEmail(email);

		// check version
		if (!cart.getVersion().equals(version)) {
			throw new ApiException(ErrorCode.ERR_CONFLICT, "Cart has been modified");
		}

		var item = findCartItemById(itemId);

		if (!item.getCart().getId().equals(cart.getId())) {
			throw new ApiException(ErrorCode.ERR_FORBIDDEN, "It's not your cart");
		}

		Product product = productRepo.findById(item.getProduct().getId())
				.orElseThrow(() -> new ApiException(ErrorCode.ERR_NOT_FOUND, "Product is not found"));

		if (Boolean.FALSE.equals(product.getIsActive())) {
			throw new ApiException(ErrorCode.ERR_BAD_REQUEST, "Product is inactive");
		}

		if (req.getQty() > product.getStock())
			throw new ApiException(ErrorCode.ERR_OUT_OF_STOCK, "Out of stock");

		item.changeQty(req.getQty());

		var items = itemRepo.findByCartIdWithProduct(cart.getId());

		return cartMapper.toCartResponse(cart, items);
	}

	@Override
	@Transactional
	public CartResponse removeItem(String email, Integer itemId, Integer version) {
		var cart = findCartByUserEmail(email);

		if (!cart.getVersion().equals(version)) {
			throw new ApiException(ErrorCode.ERR_CONFLICT, "Cart has been modified");
		}

		var item = findCartItemById(itemId);

		if (!item.getCart().getId().equals(cart.getId())) {
			throw new ApiException(ErrorCode.ERR_FORBIDDEN, "It's not your cart");
		}

		cart.removeProduct(item.getProduct().getId());
		
		var items = itemRepo.findByCartIdWithProduct(cart.getId());

	    return cartMapper.toCartResponse(cart, items);
	}

	private Cart findCartByUserEmail(String email) {
		return cartRepo.findByUserEmail(email)
				.orElseThrow(() -> new ApiException(ErrorCode.ERR_NOT_FOUND, "Cart is not found"));
	}

	private CartItem findCartItemById(int id) {
		return itemRepo.findById(id)
				.orElseThrow(() -> new ApiException(ErrorCode.ERR_NOT_FOUND, "Cart item is not found"));
	}

}
