package com.shop.catalog.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageProductRequest {

	@Min(1)
	private int page = 1;

	@Min(1)
	@Max(50)
	private int size = 10;

	@Size(max = 100)
	private String q;

	private Integer cat;

	@Pattern(regexp = "name|price", message = "sort must be 'name' or 'price'")
	private String sort = "name";

	@Pattern(regexp = "asc|desc", message = "dir must be 'asc' or 'desc'")
	private String dir = "asc";
}
