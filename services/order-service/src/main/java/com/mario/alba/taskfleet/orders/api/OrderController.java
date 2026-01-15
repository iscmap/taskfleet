package com.mario.alba.taskfleet.orders.api;

import com.mario.alba.taskfleet.orders.api.dto.CreateOrderRequest;
import com.mario.alba.taskfleet.orders.api.dto.CreateOrderResponse;
import com.mario.alba.taskfleet.orders.api.dto.OrderView;
import com.mario.alba.taskfleet.orders.application.OrderQueryService;
import com.mario.alba.taskfleet.orders.application.OrderWriteService;
import com.mario.alba.taskfleet.orders.domain.OrderStatus;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/orders")
public class OrderController {

    private final OrderWriteService writeService;
    private final OrderQueryService queryService;

    public OrderController(OrderWriteService writeService, OrderQueryService queryService) {
        this.writeService = writeService;
        this.queryService = queryService;
    }

    @PostMapping
    public ResponseEntity<CreateOrderResponse> create(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody CreateOrderRequest req
    ) {
        CreateOrderResponse created = writeService.createOrder(idempotencyKey, req);
        return ResponseEntity.status(201).body(created);
    }

    @GetMapping("/{orderId}")
    public OrderView getById(@PathVariable UUID orderId) {
        return queryService.getOrder(orderId);
    }

    @GetMapping
    public List<OrderView> search(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) OrderStatus status
    ) {
        return queryService.search(email, status);
    }
}
