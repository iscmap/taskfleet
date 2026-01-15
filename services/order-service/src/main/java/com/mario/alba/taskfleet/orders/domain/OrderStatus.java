package com.mario.alba.taskfleet.orders.domain;

public enum OrderStatus {
    CREATED,
    PAYMENT_PENDING,
    PAID,
    FULFILLMENT_PENDING,
    SHIPPED,
    CANCELLED
}
