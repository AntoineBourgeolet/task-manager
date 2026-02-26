package com.bourgeolet.task_manager.service;

import com.bourgeolet.task_manager.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
class OutboxPublishingTxService {

    private final OutboxRepository outboxRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markPublishedInNewTx(UUID id) {
        outboxRepository.markPublished(id);
    }
}