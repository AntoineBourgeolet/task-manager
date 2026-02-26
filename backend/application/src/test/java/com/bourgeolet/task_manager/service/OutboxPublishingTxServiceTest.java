package com.bourgeolet.task_manager.service;

import com.bourgeolet.task_manager.repository.OutboxRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class OutboxPublishingTxServiceTest {

    @Mock
    private OutboxRepository outboxRepository;

    @InjectMocks
    private OutboxPublishingTxService outboxPublishingTxService;

    @Test
    void markPublishedInNewTx_success(){
        UUID id = UUID.randomUUID();
        outboxPublishingTxService.markPublishedInNewTx(id);
        verify(outboxRepository).markPublished(id);
        verifyNoMoreInteractions(outboxRepository);
    }
}
