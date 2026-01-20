package com.mario.alba.taskfleet.contracts;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Generic event envelope that we send through SQS.
 * payload is a flexible Map so you can evolve events without breaking consumers quickly.
 */
public record OutboxEventMessage(
        UUID eventId,
        String eventType,
        Instant occurredAt,
        String correlationId,
        Map<String, Object> payload
) {}
