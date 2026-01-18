package com.shop.cart.controller;

import com.shop.cart.dto.request.AddCartItemRequest;
import com.shop.cart.dto.request.UpdateCartItemQtyRequest;
import com.shop.cart.service.CartService;
import com.shop.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

  private final CartService cartService;

  @GetMapping
  public ApiResponse<Object> myCart(Authentication auth) {
    return ApiResponse.ok(cartService.getMyCart(auth.getName()));
  }

  @PostMapping("/items")
  public ApiResponse<Void> add(@Valid @RequestBody AddCartItemRequest req, Authentication auth) {
    cartService.addItem(auth.getName(), req);
    return ApiResponse.ok(null);
  }

  @PatchMapping("/items/{itemId}")
  public ApiResponse<Void> updateQty(
      @PathVariable Integer itemId,
      @Valid @RequestBody UpdateCartItemQtyRequest req,
      Authentication auth
  ) {
    cartService.updateQty(auth.getName(), itemId, req);
    return ApiResponse.ok(null);
  }

  @DeleteMapping("/items/{itemId}")
  public ApiResponse<Void> remove(@PathVariable Integer itemId, Authentication auth) {
    cartService.removeItem(auth.getName(), itemId);
    return ApiResponse.ok(null);
  }
}
