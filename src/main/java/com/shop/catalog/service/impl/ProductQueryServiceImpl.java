package com.shop.catalog.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.shop.catalog.dto.request.PageProductRequest;
import com.shop.catalog.dto.request.PageProductStatusRequest;
import com.shop.catalog.dto.response.ProductResponse;
import com.shop.catalog.entity.Product;
import com.shop.catalog.service.ProductQueryService;
import com.shop.common.ErrorCode;
import com.shop.common.exception.ApiException;
import com.shop.common.pagination.PageableMapper;
import com.shop.common.util.TextNormalizer;

import jakarta.persistence.criteria.JoinType;

public class ProductQueryServiceImpl implements ProductQueryService{

	@Override
    public Page<ProductResponse> findAdminProducts(PageProductStatusRequest request) {
        if (request == null || request.getPage() == null) {
            throw new ApiException(ErrorCode.ERR_BAD_REQUEST);
        }

        PageProductRequest req = request.getPage();
        Boolean status = request.getStatus();

        Pageable pageable = PageableMapper.from(req);

        Specification<Product> spec = (root, query, cb) -> cb.conjunction();

        // q: search theo name
        String q =TextNormalizer.normalize(req.getQ());
        if (q != null) {
            String keyword = q.toLowerCase();
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("name")), "%" + keyword + "%")
            );
        }

        // cat: filter theo categoryId
        if (req.getCat() != null) {
            Integer catId = req.getCat();
            spec = spec.and((root, query, cb) -> {
                // join category để filter
                var categoryJoin = root.join("category", JoinType.INNER);
                return cb.equal(categoryJoin.get("id"), catId);
            });
        }

        // status: filter theo isActive
        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("isActive"), status));
        }

        return productRepo.findAll(spec, pageable).map(this::toResponse);
    }


}
