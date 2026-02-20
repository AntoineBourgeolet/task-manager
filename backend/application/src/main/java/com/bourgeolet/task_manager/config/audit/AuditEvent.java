package com.bourgeolet.task_manager.config.audit;


import com.bourgeolet.task_manager.config.global.GlobalConstant;

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
    public static AuditEvent ticketChangedStatus(Long ticketId, String actorId, String oldStatus, String newStatus) {
        return new AuditEvent(
                UUID.randomUUID(),
                GlobalConstant.TICKET_CHANGED_STATUS,
                GlobalConstant.AGGREGATE_TYPE_AUDIT_EVENT,
                ticketId,
                LocalTime.now(),
                actorId,
                GlobalConstant.SCHEMA_VERSION_AUDIT_EVENT,
                Map.of(GlobalConstant.LOWER_CAMEL_CASE_OLD_STATUS, oldStatus, GlobalConstant.LOWER_CAMEL_CASE_NEW_STATUS, newStatus)
        );
    }

    public static AuditEvent ticketCreated(Long ticketId, String actorId) {
        return new AuditEvent(
                UUID.randomUUID(),
                GlobalConstant.TICKET_CREATED,
                GlobalConstant.AGGREGATE_TYPE_AUDIT_EVENT,
                ticketId,
                LocalTime.now(),
                actorId,
                GlobalConstant.SCHEMA_VERSION_AUDIT_EVENT,
                null
        );
    }

    public static AuditEvent ticketChangedUserAffectee(Long ticketId, String actorId, String oldUser, String newUser) {
        return new AuditEvent(
                UUID.randomUUID(),
                GlobalConstant.TICKET_CHANGED_USER_AFFECTEE,
                GlobalConstant.AGGREGATE_TYPE_AUDIT_EVENT,
                ticketId,
                LocalTime.now(),
                actorId,
                GlobalConstant.SCHEMA_VERSION_AUDIT_EVENT,
                Map.of(GlobalConstant.LOWER_CAMEL_CASE_OLD_USER, oldUser, GlobalConstant.LOWER_CAMEL_CASE_NEW_USER, newUser)
        );
    }

    public static AuditEvent ticketDelete(Long ticketId, String actorId) {
        return new AuditEvent(
                UUID.randomUUID(),
                GlobalConstant.TICKET_DELETED,
                GlobalConstant.AGGREGATE_TYPE_AUDIT_EVENT,
                ticketId,
                LocalTime.now(),
                actorId,
                GlobalConstant.SCHEMA_VERSION_AUDIT_EVENT,
                null
        );
    }
}

