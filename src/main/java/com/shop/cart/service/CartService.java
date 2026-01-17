package com.shop.cart.service;

import com.shop.cart.dto.*;

public interface CartService {
  Object getMyCart(String email);
  void addItem(String email, AddCartItemRequest req);
  void updateQty(String email, Integer itemId, UpdateCartItemQtyRequest req);
  void removeItem(String email, Integer itemId);
}
