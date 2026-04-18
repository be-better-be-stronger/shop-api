package com.shop.cart.service;

import com.shop.cart.dto.request.AddCartItemRequest;
import com.shop.cart.dto.request.UpdateCartItemQtyRequest;
import com.shop.cart.dto.response.CartResponse;

public interface CartService {
  CartResponse getMyCart(String email);
  CartResponse addItem(String email, AddCartItemRequest req, Integer version);
  CartResponse updateQty(String email, Integer itemId, 
		  UpdateCartItemQtyRequest req, Integer version);
  CartResponse removeItem(String email, Integer itemId, Integer version);
}
