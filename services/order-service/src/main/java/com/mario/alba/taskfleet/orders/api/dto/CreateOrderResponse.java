package com.mario.alba.taskfleet.orders.api.dto;

import com.mario.alba.taskfleet.orders.domain.OrderStatus;
import java.util.UUID;

public record CreateOrderResponse(UUID orderId, OrderStatus status) {}
