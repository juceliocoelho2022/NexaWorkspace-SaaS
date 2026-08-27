package com.nexaworkspace.saas.event;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record DomainEvent(
    UUID eventId,
    UUID tenantId,
    UUID actorId,
    String type,
    String aggregateType,
    String aggregateId,
    Instant occurredAt,
    Map<String, Object> payload
) {
    public static DomainEvent of(UUID tenantId, UUID actorId, String type, String aggregateType, String aggregateId, Map<String, Object> payload) {
        return new DomainEvent(UUID.randomUUID(), tenantId, actorId, type, aggregateType, aggregateId, Instant.now(), payload == null ? Map.of() : Map.copyOf(payload));
    }
}
