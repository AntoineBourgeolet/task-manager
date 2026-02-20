package com.bourgeolet.task_manager.mapper;

import com.bourgeolet.task_manager.config.audit.AuditEvent;
import com.bourgeolet.task_manager.entity.Outbox;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class OutboxMapper {
    private final ObjectMapper objectMapper;

    public Outbox toOutbox(AuditEvent evt) {
        String payload = writeJson(evt);
        Outbox out = new Outbox();
        out.setId(evt.eventId());
        out.setAggregateType(evt.aggregateType());
        out.setAggregateId(evt.aggregateId());
        out.setEventType(evt.eventType());
        out.setEventAt(evt.occurredAt());
        out.setPayload(payload);
        out.setPublished(false);
        return out;
    }

    private String writeJson(AuditEvent evt) {
        return objectMapper.writeValueAsString(evt);
    }
}