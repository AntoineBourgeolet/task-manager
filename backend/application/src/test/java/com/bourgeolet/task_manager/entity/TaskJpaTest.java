package com.bourgeolet.task_manager.entity;

import com.bourgeolet.task_manager.dto.task.TaskStatus;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
class TaskJpaTest {

    @Autowired
    private TestEntityManager em;

    @Test
    void persist_shouldGenerateId_andDefaultStatusTodo() {
        Task task = new Task();
        task.setTitle("Implement feature X");
        task.setDescription("Details...");
        task.setPriority(5);

        Task saved = em.persistFlushFind(task);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getStatus()).isEqualTo(TaskStatus.TODO);
        assertThat(saved.getTitle()).isEqualTo("Implement feature X");
    }

    @Test
    void manyToOne_account_shouldLinkTaskToAccount() {
        // Persist account
        Account acc = new Account();
        acc.setUsername("john");
        acc.setEmail("john@test");
        em.persistAndFlush(acc);

        // Persist task with account
        Task task = new Task();
        task.setTitle("Ticket #1");
        task.setAccount(acc);
        Task saved = em.persistFlushFind(task);

        assertThat(saved.getAccount()).isNotNull();
        assertThat(saved.getAccount().getId()).isEqualTo(acc.getId());
        assertThat(saved.getAccount().getUsername()).isEqualTo("john");
    }

    @Test
    void manyToMany_tags_shouldCreateJoinRows() {
        // Tags
        Tag t1 = new Tag();
        t1.setName("backend");
        Tag t2 = new Tag();
        t2.setName("urgent");
        em.persist(t1);
        em.persist(t2);
        em.flush();

        // Task with tags
        Task task = new Task();
        task.setTitle("Fix bug");
        task.setTags(List.of(t1, t2));
        em.persistAndFlush(task);
        em.clear();

        Task reloaded = em.find(Task.class, task.getId());
        Assertions.assertNotNull(reloaded);
        assertThat(reloaded.getTags())
                .extracting(Tag::getName)
                .containsExactlyInAnyOrder("backend", "urgent");
    }

    @Test
    void update_status_shouldPersistEnumValueAsString() {
        Task task = new Task();
        task.setTitle("Flow");
        em.persistAndFlush(task);

        Task managed = em.find(Task.class, task.getId());
        Assertions.assertNotNull(managed);
        managed.setStatus(TaskStatus.TESTING);
        em.flush();
        em.clear();

        Task reloaded = em.find(Task.class, task.getId());
        Assertions.assertNotNull(reloaded);
        assertThat(reloaded.getStatus()).isEqualTo(TaskStatus.TESTING);
    }
}