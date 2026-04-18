package com.shop.cart.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shop.cart.dto.request.AddCartItemRequest;
import com.shop.cart.dto.request.UpdateCartItemQtyRequest;
import com.shop.cart.dto.response.CartResponse;
import com.shop.cart.service.CartService;
import com.shop.common.response.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

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
  public ApiResponse<CartResponse> add(@Valid @RequestBody AddCartItemRequest req, 
		  Authentication auth,
		  @RequestHeader("If-Match") Integer version) {
    var response = cartService.addItem(auth.getName(), req, version);
    return ApiResponse.ok(response);
  }

  @PatchMapping("/items/{itemId}")
  public ApiResponse<CartResponse> updateQty(
      @PathVariable Integer itemId,
      @Valid @RequestBody UpdateCartItemQtyRequest req,
      @RequestHeader("If-Match") Integer version,
      Authentication auth
  ) {
    var response = cartService.updateQty(auth.getName(), itemId, req, version);
    return ApiResponse.ok(response);
  }

  @DeleteMapping("/items/{itemId}")
  public ApiResponse<CartResponse> remove(@PathVariable Integer itemId, 
		  Authentication auth,
		  @RequestHeader("If-Match") Integer version) {
    var response = cartService.removeItem(auth.getName(), itemId, version);
    return ApiResponse.ok(response);
  }
}
