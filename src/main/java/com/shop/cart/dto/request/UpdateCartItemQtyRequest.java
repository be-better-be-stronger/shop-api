package com.shop.cart.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter
public class UpdateCartItemQtyRequest {
  @NotNull @Min(1)
  private Integer qty;
}
