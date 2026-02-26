package com.bourgeolet.task_manager.service;

import com.bourgeolet.task_manager.config.audit.AuditEvent;
import com.bourgeolet.task_manager.entity.Task;
import com.bourgeolet.task_manager.mapper.OutboxMapper;
import com.bourgeolet.task_manager.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OutboxService {

    private final OutboxRepository outboxRepository;

    private final OutboxMapper outboxMapper;

    private final OutboxPublishingTxService outboxPublishingTxService;

    public void markPublished(UUID id) {
        outboxPublishingTxService.markPublishedInNewTx(id);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void ticketStatusChangedAuditEvent(Task result, String oldStatus, String newStatus, String actor) {
        AuditEvent evt = AuditEvent.ticketChangedStatus(result.getId(), actor, oldStatus, newStatus);
        outboxRepository.save(outboxMapper.toOutbox(evt));
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void ticketChangedUserAffecteeAuditEvent(Task result, String oldUser, String newUser, String actor) {
        oldUser = Objects.requireNonNullElse(oldUser, "");
        newUser = Objects.requireNonNullElse(newUser, "");
        AuditEvent evt = AuditEvent.ticketChangedUserAffectee(result.getId(), actor, oldUser, newUser);
        outboxRepository.save(outboxMapper.toOutbox(evt));
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void ticketCreatedAuditEvent(Task result, String actor) {
        AuditEvent evt = AuditEvent.ticketCreated(result.getId(), actor);
        outboxRepository.save(outboxMapper.toOutbox(evt));
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void ticketDeleteAuditEvent(Long id, String actor) {
        AuditEvent evt = AuditEvent.ticketDelete(id, actor);
        outboxRepository.save(outboxMapper.toOutbox(evt));
    }


}
