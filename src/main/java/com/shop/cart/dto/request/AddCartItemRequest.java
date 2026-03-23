package com.shop.cart.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddCartItemRequest {
	@NotNull(message = "{cart.productId.required}")
    @Min(value = 1, message = "{cart.productId.min}")
    private Integer productId;

    @NotNull(message = "{cart.quantity.required}")
    @Min(value = 1, message = "{cart.quantity.min}")
    private Integer qty;
}

