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
	@Min(value = 0, message = "{page.min}")
    private int page = 0;

    @Min(value = 1, message = "{size.min}")
    @Max(value = 50, message = "{size.max}")
    private int size = 10;

    @Size(max = 100, message = "{product.q.size}")
    private String q;

    @Min(value = 1, message = "{product.cat.min}")
    private Integer cat;

    @Pattern(regexp = "name|price", message = "{product.sort.invalid}")
    private String sort = "name";

    @Pattern(regexp = "asc|desc", message = "{product.dir.invalid}")
    private String dir = "asc";
}
