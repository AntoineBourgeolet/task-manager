package com.bourgeolet.task_manager.mapper;

import com.bourgeolet.task_manager.config.audit.AuditEvent;
import com.bourgeolet.task_manager.entity.Outbox;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Component
@RequiredArgsConstructor
public class OutboxMapper {
    private final JsonMapper jsonMapper;

    public Outbox toOutbox(AuditEvent evt) {
        String payload = writeJson(evt);
        return Outbox.builder()
                .id(evt.eventId())
                .aggregateType(evt.aggregateType())
                .aggregateId(evt.aggregateId())
                .eventType(evt.eventType())
                .eventAt(evt.occurredAt())
                .payload(payload)
                .published(false)
                .build();
    }

    private String writeJson(AuditEvent evt) {
        return jsonMapper.writeValueAsString(evt);
    }
}