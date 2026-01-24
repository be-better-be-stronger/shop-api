 package com.shop.cart.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdateCartItemQtyRequest {
  @NotNull @Min(1)
  private Integer qty;
}


