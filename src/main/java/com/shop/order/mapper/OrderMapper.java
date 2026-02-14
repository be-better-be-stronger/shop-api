package com.shop.order.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.shop.order.dto.OrderItemResponse;
import com.shop.order.dto.OrderResponse;
import com.shop.order.entity.Order;
import com.shop.order.entity.OrderItem;

@Component
public class OrderMapper {

    public OrderItemResponse toItemRes(OrderItem i) {
        return OrderItemResponse.builder()
                .productId(i.getProduct().getId())
                .productName(i.getProduct().getName())
                .price(i.getUnitPrice())
                .qty(i.getQty())
                .lineTotal(i.getLineTotal())
                .build();
    }

    public OrderResponse toOrderRes(Order o) {
        List<OrderItemResponse> items = o.getItems() == null
                ? List.of()
                : o.getItems().stream().map(this::toItemRes).toList();

        return OrderResponse.builder()
                .orderId(o.getId())
                .total(o.getTotal())
                .status(o.getStatus().name())
                .orderDate(o.getCreatedAt())
                .items(items)
                .build();
    }
}
