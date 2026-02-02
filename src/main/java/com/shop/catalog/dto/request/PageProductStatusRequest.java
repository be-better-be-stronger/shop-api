package com.shop.catalog.dto.request;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageProductStatusRequest {

    @Valid
    private PageProductRequest page;
    private Boolean status;
}

