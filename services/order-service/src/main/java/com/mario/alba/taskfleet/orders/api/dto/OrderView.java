package com.mario.alba.taskfleet.orders.api.dto;

import com.mario.alba.taskfleet.orders.domain.OrderStatus;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderView(
        UUID orderId,
        String customerEmail,
        OrderStatus status,
        Instant createdAt,
        List<Item> items
) {
    public record Item(String sku, int quantity) {}
}
