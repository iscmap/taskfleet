package com.mario.alba.taskfleet.orders.application;

import com.mario.alba.taskfleet.orders.api.dto.CreateOrderRequest;
import com.mario.alba.taskfleet.orders.api.dto.CreateOrderResponse;
import com.mario.alba.taskfleet.orders.domain.OrderStatus;
import com.mario.alba.taskfleet.orders.infra.jpa.IdempotencyKeyEntity;
import com.mario.alba.taskfleet.orders.infra.jpa.OrderEntity;
import com.mario.alba.taskfleet.orders.infra.jpa.OrderItemEntity;
import com.mario.alba.taskfleet.orders.infra.repo.IdempotencyRepository;
import com.mario.alba.taskfleet.orders.infra.repo.OrderRepository;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mario.alba.taskfleet.contracts.OutboxEventMessage;
import com.mario.alba.taskfleet.orders.infra.jpa.OutboxEventEntity;
import com.mario.alba.taskfleet.orders.infra.repo.OutboxRepository;
import java.time.Instant;
import java.util.Map;


@Service
public class OrderWriteService {

    private final OrderRepository orders;
    private final IdempotencyRepository idemRepo;
    private final RequestHasher hasher;
    private final OutboxRepository outbox;
    private final ObjectMapper mapper;

    public OrderWriteService(
            OrderRepository orders,
            IdempotencyRepository idemRepo,
            OutboxRepository outbox,
            com.fasterxml.jackson.databind.ObjectMapper mapper
    ) {
        this.orders = orders;
        this.idemRepo = idemRepo;
        this.outbox = outbox;
        this.hasher = new RequestHasher(mapper);
        this.mapper = mapper;
    }

    @Transactional
    public CreateOrderResponse createOrder(String idempotencyKey, CreateOrderRequest req) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new MissingIdempotencyKeyException();
        }

        String json = hasher.stableJson(req);
        String requestHash = hasher.sha256(json);

        // 1) If key already exists: return same order if hash matches; else conflict
        var existing = idemRepo.findByIdempotencyKey(idempotencyKey);
        if (existing.isPresent()) {
            var rec = existing.get();
            if (!rec.getRequestHash().equals(requestHash)) {
                throw new IdempotencyKeyConflictException();
            }
            return new CreateOrderResponse(rec.getOrderId(), OrderStatus.CREATED);
        }

        // 2) Create new order
        UUID orderId = UUID.randomUUID();
        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        order.setCustomerEmail(req.customerEmail());
        order.setStatus(OrderStatus.CREATED.name());
        order.setCreatedAt(Instant.now());

        for (var item : req.items()) {
            OrderItemEntity oi = new OrderItemEntity();
            oi.setId(UUID.randomUUID());
            oi.setOrder(order);
            oi.setSku(item.sku());
            oi.setQuantity(item.quantity());
            order.getItems().add(oi);
        }

        orders.save(order);

        // 3) Persist idempotency record (unique constraint prevents duplicates on race)
        IdempotencyKeyEntity idem = new IdempotencyKeyEntity();
        idem.setId(UUID.randomUUID());
        idem.setIdempotencyKey(idempotencyKey);
        idem.setRequestHash(requestHash);
        idem.setOrderId(orderId);
        idem.setCreatedAt(Instant.now());
        idemRepo.save(idem);

        var eventId = UUID.randomUUID();
        var envelope = new OutboxEventMessage(
                eventId,
                "OrderCreated",
                Instant.now(),
                null, // later we’ll propagate correlation id
                Map.of(
                        "orderId", orderId.toString(),
                        "customerEmail", req.customerEmail()
                )
        );

        String eventJson;
        try {
            eventJson = mapper.writeValueAsString(envelope);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to serialize outbox event", e);
        }

        OutboxEventEntity ob = new OutboxEventEntity();
        ob.setId(eventId);
        ob.setEventType("OrderCreated");
        ob.setPayloadJson(eventJson);
        ob.setCorrelationId(null);
        ob.setOccurredAt(Instant.now());
        ob.setPublishedAt(null);
        outbox.save(ob);

        return new CreateOrderResponse(orderId, OrderStatus.CREATED);
    }
}
