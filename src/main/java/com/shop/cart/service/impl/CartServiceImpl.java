package com.shop.cart.service.impl;

import com.shop.cart.dto.*;
import com.shop.cart.entity.CartItem;
import com.shop.cart.repository.*;
import com.shop.catalog.repository.ProductRepository;
import com.shop.common.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements com.shop.cart.service.CartService {

  private final CartRepository cartRepo;
  private final CartItemRepository itemRepo;
  private final ProductRepository productRepo;

  @Override
  public Object getMyCart(String email) {
    var cart = cartRepo.findByUserEmail(email)
        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Cart not found"));

    var items = itemRepo.findByCartId(cart.getId());

    // tạm trả thẳng map cho nhanh (sau sẽ DTO hóa)
    Map<String, Object> res = new HashMap<>();
    res.put("cartId", cart.getId());
    res.put("items", items);
    return res;
  }

  @Override
  @Transactional
  public void addItem(String email, AddCartItemRequest req) {
    var cart = cartRepo.findByUserEmail(email)
        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Cart not found"));

    var p = productRepo.findById(req.getProductId())
        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Product not found"));

    if (Boolean.FALSE.equals(p.getIsActive())) {
      throw new ApiException(HttpStatus.BAD_REQUEST, "Product is inactive");
    }

    var existing = itemRepo.findByCartIdAndProductId(cart.getId(), p.getId());
    if (existing.isPresent()) {
      var item = existing.get();
      item.setQty(item.getQty() + req.getQty());
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
        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Cart not found"));

    var item = itemRepo.findById(itemId)
        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Item not found"));

    if (!item.getCart().getId().equals(cart.getId())) {
      throw new ApiException(HttpStatus.FORBIDDEN, "Not your cart item");
    }

    item.setQty(req.getQty());
  }

  @Override
  @Transactional
  public void removeItem(String email, Integer itemId) {
    var cart = cartRepo.findByUserEmail(email)
        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Cart not found"));

    var item = itemRepo.findById(itemId)
        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Item not found"));

    if (!item.getCart().getId().equals(cart.getId())) {
      throw new ApiException(HttpStatus.FORBIDDEN, "Not your cart item");
    }

    itemRepo.delete(item);
  }
}
