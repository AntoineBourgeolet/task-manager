package com.bourgeolet.task_manager.service;

import com.bourgeolet.task_manager.config.audit.AuditEvent;
import com.bourgeolet.task_manager.dto.task.TaskResponseDTO;
import com.bourgeolet.task_manager.mapper.OutboxMapper;
import com.bourgeolet.task_manager.repository.OutboxRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class OutboxService {

    OutboxRepository outboxRepository;

    OutboxMapper outboxMapper;

    public OutboxService(OutboxRepository outboxRepository, OutboxMapper outboxMapper) {
        this.outboxRepository = outboxRepository;
        this.outboxMapper = outboxMapper;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markPublishedAsync(UUID id) {
        outboxRepository.markPublished(id);
    }

    public void statusChangedAuditEvent(TaskResponseDTO result, String oldStatus, String newStatus){
        AuditEvent evt = AuditEvent.statusChanged(result.id(), result.userAffectee(), oldStatus, newStatus, "Mise a jours du status");
        outboxRepository.save(outboxMapper.toOutbox(evt));
    }
}
