package com.shop.cart.dto.response;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Value;

@Getter
@Value
public class CartItemResponse {
  Integer itemId;
  Integer productId;
  String productName;
  BigDecimal unitPrice;
  Integer qty;
  BigDecimal lineTotal;
}
