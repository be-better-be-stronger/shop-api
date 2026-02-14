package com.shop.catalog.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpsertProductRequest {

	@NotBlank(message = "{product.name.required}")
    @Size(max = 100, message = "{product.name.size}")
    private String name;

    @NotNull(message = "{product.stock.required}")
    @Min(value = 1, message = "{product.stock.min}")
    private Integer stock;

    @NotNull(message = "{product.price.required}")
    @DecimalMin(value = "0.0", inclusive = false, message = "{product.price.min}")
    @Digits(integer = 10, fraction = 2, message = "{product.price.digits}")
    private BigDecimal price;

    @NotNull(message = "{product.categoryId.required}")
    @Min(value = 1, message = "{product.cat.min}")
    private Integer categoryId;

    private Boolean isActive;

    private String imageUrl;

    @Size(max = 2000, message = "{product.description.size}")
    private String description;
}

