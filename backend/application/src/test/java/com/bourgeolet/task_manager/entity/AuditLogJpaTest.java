package com.bourgeolet.task_manager.entity;

import jakarta.persistence.PersistenceException;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@DataJpaTest
class AuditLogJpaTest {

    @Autowired
    private TestEntityManager em;

    @Test
    void persist_shouldStoreAllFields_andReadBack() {
        // Arrange
        UUID id = UUID.randomUUID();
        AuditLog log = new AuditLog();
        log.setId(id);
        log.setTicketId(123L);
        log.setEventType("TICKET_CREATED");
        log.setWho("john.doe");
        log.setWhenAt(LocalTime.of(10, 15, 30));

        // texte long pour exercer la colonne TEXT
        String largeData = "x".repeat(40_000); // suffisamment grand pour TEXT
        log.setData(largeData);

        // Act
        em.persistAndFlush(log);
        em.clear(); // on vide le contexte de persistance pour relire depuis la DB

        AuditLog reloaded = em.find(AuditLog.class, id);

        // Assert
        assertThat(reloaded).isNotNull();
        assertThat(reloaded.getId()).isEqualTo(id);
        assertThat(reloaded.getTicketId()).isEqualTo(123L);
        assertThat(reloaded.getEventType()).isEqualTo("TICKET_CREATED");
        assertThat(reloaded.getWho()).isEqualTo("john.doe");
        assertThat(reloaded.getWhenAt()).isEqualTo(LocalTime.of(10, 15, 30));
        assertThat(reloaded.getData()).hasSize(40_000);
    }

    @Test
    void update_shouldModifyExistingRow() {
        // Arrange
        AuditLog log = new AuditLog();
        UUID id = UUID.randomUUID();
        log.setId(id);
        log.setTicketId(1L);
        log.setEventType("TICKET_CREATED");
        log.setWho("alice");
        log.setWhenAt(LocalTime.NOON);
        log.setData("initial");

        em.persistAndFlush(log);

        // Act : on met à jour quelques champs
        AuditLog managed = em.find(AuditLog.class, id);
        Assertions.assertNotNull(managed);
        managed.setEventType("TICKET_STATUS_CHANGED");
        managed.setData("updated-payload");
        em.flush();
        em.clear();

        // Assert
        AuditLog reloaded = em.find(AuditLog.class, id);
        Assertions.assertNotNull(reloaded);
        assertThat(reloaded.getEventType()).isEqualTo("TICKET_STATUS_CHANGED");
        assertThat(reloaded.getData()).isEqualTo("updated-payload");
    }

    @Test
    void persist_withoutId_shouldFailBecauseIdIsNotGenerated() {
        // Arrange
        AuditLog log = new AuditLog();
        log.setTicketId(999L);
        log.setEventType("ANY");
        log.setWho("system");
        log.setWhenAt(LocalTime.MIDNIGHT);
        log.setData("payload");

        // Act + Assert
        assertThatThrownBy(() -> {
            em.persistAndFlush(log);
        })
                .isInstanceOf(PersistenceException.class);
    }
}