package com.shop.catalog.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpsertProductRequest {

	@NotBlank(message = "Name must not be blank")
	@Size(max = 100, message = "Name must be at most 100 characters")
	private String name;

	@NotNull(message = "Stock is required")
	@Min(value = 1, message = "Stock must be >= 1")
	private Integer stock;

	@NotNull(message = "Price is required")
	@DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
	@Digits(integer = 10, fraction = 2, message = "Price must have up to 10 integer digits and 2 decimal places")
	private BigDecimal price;

	@NotNull(message = "CategoryId is required")
	@Positive(message = "CategoryId must be a positive number")
	private Integer categoryId;

	private Boolean isActive;
}