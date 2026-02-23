package com.shop.catalog.dto.response;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class ProductResponse {	
	Integer id;
	String name;
	BigDecimal price;
	Integer stock;
	Integer categoryId;
	String categoryName;
	Boolean isActive;
	String imageUrl;
}
