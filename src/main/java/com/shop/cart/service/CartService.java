package com.shop.cart.service;

import com.shop.cart.dto.request.AddCartItemRequest;
import com.shop.cart.dto.request.UpdateCartItemQtyRequest;
import com.shop.cart.dto.response.CartResponse;

public interface CartService {
  CartResponse getMyCart(String email);
  void addItem(String email, AddCartItemRequest req);
  void updateQty(String email, Integer itemId, UpdateCartItemQtyRequest req);
  void removeItem(String email, Integer itemId);
}
