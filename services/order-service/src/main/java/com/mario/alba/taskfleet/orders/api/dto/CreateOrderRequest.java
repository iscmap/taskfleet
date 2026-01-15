package com.mario.alba.taskfleet.orders.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import java.util.List;

public record CreateOrderRequest(
        @Email @NotBlank String customerEmail,
        @NotEmpty List<Item> items
) {
    public record Item(
            @NotBlank String sku,
            @Positive int quantity
    ) {}
}
