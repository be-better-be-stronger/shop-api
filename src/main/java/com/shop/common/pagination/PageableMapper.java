package com.shop.common.pagination;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.shop.catalog.dto.request.PageProductRequest;


public final class PageableMapper {

    private PageableMapper() {}

    public static Pageable from(PageProductRequest req) {
        int pageIndex = Math.max(req.getPage(), 1) - 1;
        int size = Math.min(Math.max(req.getSize(), 1), 50);

        Sort.Direction direction =
                "desc".equalsIgnoreCase(req.getDir())
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        String sortField =
                "price".equalsIgnoreCase(req.getSort())
                ? "price"
                : "name";

        return PageRequest.of(pageIndex, size, Sort.by(direction, sortField));
    }
}
