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
    
    public static PageProductStatusRequest of(PageProductRequest page, Boolean status) {
        PageProductStatusRequest req = new PageProductStatusRequest();
        req.setPage(page);
        req.setStatus(status);
        return req;
    }
}

