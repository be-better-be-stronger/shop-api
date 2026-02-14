package com.shop.catalog.dto.response;

import lombok.Builder;
import lombok.Value;

@Value // private final
@Builder
public class CategoryResponse {
	Integer id;
	String name;
}
