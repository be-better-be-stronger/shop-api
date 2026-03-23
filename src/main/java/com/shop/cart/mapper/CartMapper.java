package com.shop.cart.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.shop.cart.dto.response.CartResponse;
import com.shop.cart.entity.Cart;

@Mapper(componentModel = "spring", uses = CartItemMapper.class)
public interface CartMapper {

    @org.mapstruct.Mapping(target = "cartId", source = "id")
    @Mapping(target = "total", expression = "java(cart.getSubtotal())")
    CartResponse toResponse(Cart cart);
}
