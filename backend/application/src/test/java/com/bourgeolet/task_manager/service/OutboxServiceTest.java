package com.bourgeolet.task_manager.service;

import com.bourgeolet.task_manager.config.audit.AuditEvent;
import com.bourgeolet.task_manager.entity.Task;
import com.bourgeolet.task_manager.mapper.OutboxMapper;
import com.bourgeolet.task_manager.repository.OutboxRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OutboxServiceTest {

    @Mock
    private OutboxRepository outboxRepository;

    @Mock
    private OutboxMapper outboxMapper;

    @Mock
    OutboxPublishingTxService outboxPublishingTxService;

    @InjectMocks
    private OutboxService outboxService;

    @Test
    void markPublishedAsync_shouldDelegateToRepository() {
        UUID id = UUID.randomUUID();

        outboxService.markPublished(id);

        verify(outboxPublishingTxService).markPublishedInNewTx(id);
        verifyNoMoreInteractions(outboxPublishingTxService);

        verifyNoInteractions(outboxRepository);
        verifyNoInteractions(outboxMapper);
    }

    @Test
    void ticketStatusChangedAuditEvent_shouldBuildEventAndPersist() {
        Task task = new Task();
        task.setId(42L);
        String actor = "john";
        String oldStatus = "TODO";
        String newStatus = "DONE";

        AuditEvent eventMock = mock(AuditEvent.class);

        try (MockedStatic<AuditEvent> mocked = mockStatic(AuditEvent.class)) {
            mocked.when(() -> AuditEvent.ticketChangedStatus(42L, actor, oldStatus, newStatus))
                    .thenReturn(eventMock);

            when(outboxMapper.toOutbox(eventMock)).thenReturn(null);

            outboxService.ticketStatusChangedAuditEvent(task, oldStatus, newStatus, actor);

            mocked.verify(() -> AuditEvent.ticketChangedStatus(42L, actor, oldStatus, newStatus));
            verify(outboxMapper).toOutbox(eventMock);
            verify(outboxRepository).save(null);

            verifyNoMoreInteractions(outboxMapper, outboxRepository);
        }
    }

    @Test
    void ticketChangedUserAffecteeAuditEvent_shouldBuildEventWithProvidedUsersAndPersist() {
        Task task = new Task();
        task.setId(7L);
        String actor = "alice";
        String oldUser = "bob";
        String newUser = "charlie";

        AuditEvent eventMock = mock(AuditEvent.class);

        try (MockedStatic<AuditEvent> mocked = mockStatic(AuditEvent.class)) {
            mocked.when(() -> AuditEvent.ticketChangedUserAffectee(7L, actor, oldUser, newUser))
                    .thenReturn(eventMock);

            when(outboxMapper.toOutbox(eventMock)).thenReturn(null);

            outboxService.ticketChangedUserAffecteeAuditEvent(task, oldUser, newUser, actor);

            mocked.verify(() -> AuditEvent.ticketChangedUserAffectee(7L, actor, oldUser, newUser));
            verify(outboxMapper).toOutbox(eventMock);
            verify(outboxRepository).save(null);

            verifyNoMoreInteractions(outboxMapper, outboxRepository);
        }
    }

    @Test
    void ticketChangedUserAffecteeAuditEvent_shouldNormalizeNullUsersToEmptyString() {
        // Arrange
        Task task = new Task();
        task.setId(7L);
        String actor = "alice";
        String oldUser = null;
        String newUser = null;

        AuditEvent eventMock = mock(AuditEvent.class);

        try (MockedStatic<AuditEvent> mocked = mockStatic(AuditEvent.class)) {
            mocked.when(() -> AuditEvent.ticketChangedUserAffectee(7L, actor, "", ""))
                    .thenReturn(eventMock);

            when(outboxMapper.toOutbox(eventMock)).thenReturn(null);

            outboxService.ticketChangedUserAffecteeAuditEvent(task, oldUser, newUser, actor);

            mocked.verify(() -> AuditEvent.ticketChangedUserAffectee(7L, actor, "", ""));
            verify(outboxMapper).toOutbox(eventMock);
            verify(outboxRepository).save(null);

            verifyNoMoreInteractions(outboxMapper, outboxRepository);
        }
    }

    @Test
    void ticketCreatedAuditEvent_shouldBuildEventAndPersist() {
        Task task = new Task();
        task.setId(100L);
        String actor = "system";

        AuditEvent eventMock = mock(AuditEvent.class);

        try (MockedStatic<AuditEvent> mocked = mockStatic(AuditEvent.class)) {
            mocked.when(() -> AuditEvent.ticketCreated(100L, actor))
                    .thenReturn(eventMock);

            when(outboxMapper.toOutbox(eventMock)).thenReturn(null);

            outboxService.ticketCreatedAuditEvent(task, actor);

            mocked.verify(() -> AuditEvent.ticketCreated(100L, actor));
            verify(outboxMapper).toOutbox(eventMock);
            verify(outboxRepository).save(null);

            verifyNoMoreInteractions(outboxMapper, outboxRepository);
        }
    }

    @Test
    void ticketDeleteAuditEvent_shouldBuildEventAndPersist() {
        long id = 55L;
        String actor = "deleter";

        AuditEvent eventMock = mock(AuditEvent.class);

        try (MockedStatic<AuditEvent> mocked = mockStatic(AuditEvent.class)) {
            mocked.when(() -> AuditEvent.ticketDelete(id, actor))
                    .thenReturn(eventMock);

            when(outboxMapper.toOutbox(eventMock)).thenReturn(null);

            outboxService.ticketDeleteAuditEvent(id, actor);

            mocked.verify(() -> AuditEvent.ticketDelete(id, actor));
            verify(outboxMapper).toOutbox(eventMock);
            verify(outboxRepository).save(null);

            verifyNoMoreInteractions(outboxMapper, outboxRepository);
        }
    }
}