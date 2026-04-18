package com.shop.cart.mapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.shop.cart.dto.response.CartItemResponse;
import com.shop.cart.dto.response.CartResponse;
import com.shop.cart.entity.Cart;
import com.shop.cart.entity.CartItem;

@Component
public class CartMapper {

	public CartItemResponse toItemResponse(CartItem item) {
        BigDecimal lineTotal = item.computeLineTotal();

        return new CartItemResponse(
                item.getId(),
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getUnitPrice(),
                item.getQty(),
                lineTotal
        );
    }

    public CartResponse toCartResponse(Cart cart, List<CartItem> items) {
    	BigDecimal total = BigDecimal.ZERO;
        int totalItems = 0;

        var resItems = new ArrayList<CartItemResponse>();

        for (var item : items) {
            BigDecimal lineTotal = item.computeLineTotal();
            total = total.add(lineTotal);
            totalItems += item.getQty();

            resItems.add(new CartItemResponse(
                    item.getId(),
                    item.getProduct().getId(),
                    item.getProduct().getName(),
                    item.getUnitPrice(),
                    item.getQty(),
                    lineTotal
            ));
        }

        return new CartResponse(
                cart.getId(),
                resItems,
                total,
                totalItems,
                cart.getVersion()
        );
    }
}
