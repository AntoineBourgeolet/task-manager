package com.bourgeolet.task_manager.entity;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import jakarta.persistence.PersistenceException;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.LocalTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
class OutboxJpaTest {

    @Autowired
    private TestEntityManager em;

    @Test
    void persist_shouldStoreAllFields_andReadBack() {
        UUID id = UUID.randomUUID();
        Outbox outbox = new Outbox();
        outbox.setId(id);
        outbox.setAggregateType("TASK");
        outbox.setAggregateId(123L);
        outbox.setEventType("TICKET_CREATED");
        outbox.setEventAt(LocalTime.of(9, 30));
        outbox.setPublished(Boolean.FALSE);
        outbox.setPublishedAt(null);

        String bigPayload = "x".repeat(50_000);
        outbox.setPayload(bigPayload);

        em.persistAndFlush(outbox);
        em.clear();

        Outbox reloaded = em.find(Outbox.class, id);
        assertThat(reloaded).isNotNull();
        assertThat(reloaded.getAggregateType()).isEqualTo("TASK");
        assertThat(reloaded.getAggregateId()).isEqualTo(123L);
        assertThat(reloaded.getEventType()).isEqualTo("TICKET_CREATED");
        assertThat(reloaded.getEventAt()).isEqualTo(LocalTime.of(9, 30));
        assertThat(reloaded.getPublished()).isFalse();
        assertThat(reloaded.getPublishedAt()).isNull();
        assertThat(reloaded.getPayload()).hasSize(50_000);
    }

    @Test
    void update_shouldChangePublishedFlagsAndTimes() {
        UUID id = UUID.randomUUID();
        Outbox outbox = new Outbox();
        outbox.setId(id);
        outbox.setAggregateType("TASK");
        outbox.setAggregateId(1L);
        outbox.setEventType("ANY");
        outbox.setEventAt(LocalTime.NOON);
        outbox.setPayload("payload");
        outbox.setPublished(Boolean.FALSE);
        em.persistAndFlush(outbox);

        Outbox managed = em.find(Outbox.class, id);
        Assertions.assertNotNull(managed);
        managed.setPublished(Boolean.TRUE);
        managed.setPublishedAt(LocalTime.of(13, 37));
        em.flush();
        em.clear();

        Outbox reloaded = em.find(Outbox.class, id);
        Assertions.assertNotNull(reloaded);
        assertThat(reloaded.getPublished()).isTrue();
        assertThat(reloaded.getPublishedAt()).isEqualTo(LocalTime.of(13, 37));
    }

    @Test
    void persist_withoutId_shouldFail() {
        Outbox outbox = new Outbox();
        // pas d'ID
        outbox.setAggregateType("TASK");
        outbox.setAggregateId(1L);
        outbox.setEventType("X");
        outbox.setEventAt(LocalTime.MIDNIGHT);
        outbox.setPayload("p");

        assertThatThrownBy(() -> em.persistAndFlush(outbox))
                .isInstanceOf(PersistenceException.class);
    }
}