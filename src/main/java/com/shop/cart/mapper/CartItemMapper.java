package com.shop.cart.mapper;

import org.mapstruct.Mapper;

import com.shop.cart.dto.response.CartItemResponse;
import com.shop.cart.entity.CartItem;

@Mapper(componentModel = "spring")
public interface CartItemMapper {
    CartItemResponse toResponse(CartItem item);
}