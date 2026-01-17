package com.shop.cart.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter
public class AddCartItemRequest {
  @NotNull
  private Integer productId;

  @NotNull @Min(1)
  private Integer qty;
}
