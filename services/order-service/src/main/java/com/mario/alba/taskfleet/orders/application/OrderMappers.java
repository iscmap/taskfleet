package com.mario.alba.taskfleet.orders.application;

import com.mario.alba.taskfleet.orders.api.dto.OrderView;
import com.mario.alba.taskfleet.orders.domain.OrderStatus;
import com.mario.alba.taskfleet.orders.infra.jpa.OrderEntity;

public final class OrderMappers {
    private OrderMappers() {}

    public static OrderView toView(OrderEntity e) {
        return new OrderView(
                e.getId(),
                e.getCustomerEmail(),
                OrderStatus.valueOf(e.getStatus()),
                e.getCreatedAt(),
                e.getItems().stream()
                        .map(i -> new OrderView.Item(i.getSku(), i.getQuantity()))
                        .toList()
        );
    }
}
