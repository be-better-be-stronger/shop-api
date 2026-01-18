package com.shop.cart.dto.response;

import lombok.*;
import java.math.BigDecimal;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class CartItemResponse {
  private Integer itemId;
  private Integer productId;
  private String productName;
  private BigDecimal unitPrice;
  private Integer qty;
  private BigDecimal lineTotal;
}
