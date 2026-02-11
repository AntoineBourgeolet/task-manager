package com.bourgeolet.task_manager.config.audit;


import java.time.Instant;
import java.time.LocalTime;
import java.util.Map;
import java.util.UUID;

public record AuditEvent(
        UUID eventId,
        String eventType,
        String aggregateType,
        Long aggregateId,
        LocalTime occurredAt,
        String actorId,
        int schemaVersion,
        Map<String, Object> data
) {
    public static AuditEvent statusChanged(Long ticketId, String actorId, String oldStatus, String newStatus, String comment) {
        return new AuditEvent(
                UUID.randomUUID(),
                "STATUS_CHANGED",
                "TICKET",
                ticketId,
                LocalTime.now(),
                actorId,
                1,
                Map.of("oldStatus", oldStatus, "newStatus", newStatus, "comment", comment)
        );
    }

    public static AuditEvent ticketCreated(Long ticketId, String actorId, String title) {
        return new AuditEvent(
                UUID.randomUUID(),
                "TICKET_CREATED",
                "TICKET",
                ticketId,
                LocalTime.now(),
                actorId,
                1,
                Map.of("title", title)
        );
    }
}

