 package com.shop.cart.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class UpdateCartItemQtyRequest {
	@NotNull(message = "{cart.quantity.required}")
    @Min(value = 1, message = "{cart.quantity.min}")
  private Integer qty;
}


