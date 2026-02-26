package com.bourgeolet.task_manager.service;

import com.bourgeolet.task_manager.command.TaskPatchCommand;
import com.bourgeolet.task_manager.dto.task.TaskStatus;
import com.bourgeolet.task_manager.entity.Account;
import com.bourgeolet.task_manager.entity.Tag;
import com.bourgeolet.task_manager.entity.Task;
import com.bourgeolet.task_manager.exception.account.AccountNotFoundException;
import com.bourgeolet.task_manager.exception.task.TaskNotFoundException;
import com.bourgeolet.task_manager.repository.AccountRepository;
import com.bourgeolet.task_manager.repository.TagRepository;
import com.bourgeolet.task_manager.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    TaskRepository taskRepository;

    @Mock
    AccountRepository accountRepository;

    @Mock
    TagRepository tagRepository;

    @Mock
    OutboxService outboxService;

    @InjectMocks
    TaskService taskService;

    Task task;
    Account accountOld;
    Account accountNew;

    @BeforeEach
    void setUp() {
        task = new Task();
        task.setId(42L);
        task.setTitle("old-title");
        task.setDescription("old-desc");
        task.setPriority(1);
        task.setStatus(TaskStatus.TODO);
        accountOld = new Account();
        accountOld.setUsername("oldUser");
        task.setAccount(accountOld);
        task.setTags(List.of());
        accountNew = new Account();
        accountNew.setUsername("newUser");
    }

    @Test
    void create_shouldSaveTaskAndEmitAuditEvent() {
        Task toSave = new Task();
        toSave.setTitle("title");
        Task saved = new Task();
        saved.setId(100L);
        saved.setTitle("title");
        when(taskRepository.save(toSave)).thenReturn(saved);

        Task result = taskService.create(toSave, "actor");

        assertThat(result).isSameAs(saved);
        verify(taskRepository).save(toSave);
        verify(outboxService).ticketCreatedAuditEvent(saved, "actor");
        verifyNoMoreInteractions(outboxService);
    }

    @Test
    void deleteTask_shouldDeleteAndEmitAuditEvent_whenTaskExists() {
        when(taskRepository.findById(42L)).thenReturn(Optional.of(task));

        taskService.deleteTask(42L, "actor");

        InOrder inOrder = inOrder(taskRepository, outboxService);
        inOrder.verify(taskRepository).findById(42L);
        inOrder.verify(outboxService).ticketDeleteAuditEvent(42L, "actor");
        inOrder.verify(taskRepository).delete(task);
        verifyNoMoreInteractions(outboxService);
    }

    @Test
    void deleteTask_shouldThrow_whenTaskNotFound() {
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.deleteTask(999L, "actor"))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining("999");

        verify(taskRepository).findById(999L);
        verifyNoInteractions(outboxService);
        verify(taskRepository, never()).delete(any());
    }

    @Test
    void getTaskById_shouldReturnTask_whenExists() {
        when(taskRepository.findById(42L)).thenReturn(Optional.of(task));

        Task result = taskService.getTaskById(42L);

        assertThat(result).isSameAs(task);
        verify(taskRepository).findById(42L);
    }

    @Test
    void getTaskById_shouldThrow_whenNotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.getTaskById(1L))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining("1");

        verify(taskRepository).findById(1L);
    }

    @Test
    void findAll_shouldReturnAll() {
        List<Task> all = List.of(new Task(), new Task(), new Task());
        when(taskRepository.findAll()).thenReturn(all);

        List<Task> result = taskService.findAll();

        assertThat(result).isSameAs(all);
        verify(taskRepository).findAll();
    }

    @Test
    void patchTask_shouldPatchAllFields_whenPresentFlagsTrue_andEmitBothEvents() {
        when(taskRepository.findById(42L)).thenReturn(Optional.of(task));
        when(accountRepository.findAccountByUsername("newUser")).thenReturn(Optional.ofNullable(accountNew));
        Tag tag1 = new Tag();
        tag1.setId(1L);
        Tag tag2 = new Tag();
        tag2.setId(2L);
        when(tagRepository.findById(1L)).thenReturn(Optional.of(tag1));
        when(tagRepository.findById(2L)).thenReturn(Optional.of(tag2));
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        TaskPatchCommand cmd = TaskPatchCommand.builder()
                .taskId(42L)
                .actor("actor")
                .statusPresent(true)
                .status(TaskStatus.DONE)
                .titlePresent(true)
                .title("new-title")
                .descriptionPresent(true)
                .description("new-desc")
                .priorityPresent(true)
                .priority(9)
                .tagsPresent(true)
                .tagIds(List.of(1L, 2L))
                .userAffecteePresent(true)
                .userAffectee("newUser")
                .build();

        Task result = taskService.patchTask(cmd);

        assertThat(result.getStatus()).isEqualTo(TaskStatus.DONE);
        assertThat(result.getAccount()).isSameAs(accountNew);
        assertThat(result.getTitle()).isEqualTo("new-title");
        assertThat(result.getDescription()).isEqualTo("new-desc");
        assertThat(result.getPriority()).isEqualTo(9);
        assertThat(result.getTags()).containsExactly(tag1, tag2);
        verify(outboxService).ticketStatusChangedAuditEvent(result, "TODO", "DONE", "actor");
        verify(outboxService).ticketChangedUserAffecteeAuditEvent(result, "oldUser", "newUser", "actor");
    }

    @Test
    void patchTask_shouldNotApplyValues_whenPresentFlagsFalse_noEvents() {
        when(taskRepository.findById(42L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        TaskPatchCommand cmd = TaskPatchCommand.builder()
                .taskId(42L)
                .actor("actor")
                .statusPresent(false)
                .status(TaskStatus.DONE)
                .titlePresent(false)
                .title("new-title")
                .descriptionPresent(false)
                .description("new-desc")
                .priorityPresent(false)
                .priority(9)
                .tagsPresent(false)
                .tagIds(List.of(1L, 2L))
                .userAffecteePresent(false)
                .userAffectee("anotherUser")
                .build();

        Task result = taskService.patchTask(cmd);

        assertThat(result.getStatus()).isEqualTo(TaskStatus.TODO);
        assertThat(result.getAccount()).isSameAs(accountOld);
        assertThat(result.getTitle()).isEqualTo("old-title");
        assertThat(result.getDescription()).isEqualTo("old-desc");
        assertThat(result.getPriority()).isEqualTo(1);
        assertThat(result.getTags()).isEmpty();
        verify(outboxService, never()).ticketStatusChangedAuditEvent(any(), any(), any(), any());
        verify(outboxService, never()).ticketChangedUserAffecteeAuditEvent(any(), any(), any(), any());
        verifyNoInteractions(accountRepository);
        verifyNoInteractions(tagRepository);
    }

    @Test
    void patchTask_shouldEmitStatusEventOnly_whenStatusPresentAndChanged_andUserUnchanged() {
        when(taskRepository.findById(42L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        TaskPatchCommand cmd = TaskPatchCommand.builder()
                .taskId(42L)
                .actor("actor")
                .statusPresent(true)
                .status(TaskStatus.DOING)
                .titlePresent(false)
                .descriptionPresent(false)
                .priorityPresent(false)
                .tagsPresent(false)
                .userAffecteePresent(false)
                .userAffectee("oldUser")
                .build();

        Task result = taskService.patchTask(cmd);

        assertThat(result.getStatus()).isEqualTo(TaskStatus.DOING);
        verify(outboxService).ticketStatusChangedAuditEvent(result, "TODO", "DOING", "actor");
        verify(outboxService, never()).ticketChangedUserAffecteeAuditEvent(any(), any(), any(), any());
    }

    @Test
    void patchTask_shouldClearTags_whenTagsPresentTrueWithEmptyList() {
        Tag initialTag = new Tag();
        initialTag.setId(9L);
        task.setTags(List.of(initialTag));
        when(taskRepository.findById(42L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        TaskPatchCommand cmd = TaskPatchCommand.builder()
                .taskId(42L)
                .actor("actor")
                .statusPresent(false)
                .titlePresent(false)
                .descriptionPresent(false)
                .priorityPresent(false)
                .tagsPresent(true)
                .tagIds(List.of())
                .userAffecteePresent(false)
                .userAffectee("oldUser")
                .build();

        Task result = taskService.patchTask(cmd);

        assertThat(result.getTags()).isEmpty();
        verifyNoInteractions(tagRepository);
        verify(outboxService, never()).ticketStatusChangedAuditEvent(any(), any(), any(), any());
        verify(outboxService, never()).ticketChangedUserAffecteeAuditEvent(any(), any(), any(), any());
    }

    @Test
    void patchTask_shouldThrow_whenUserPresentAndNewUserNotFound() {
        when(taskRepository.findById(42L)).thenReturn(Optional.of(task));
        when(accountRepository.findAccountByUsername("ghost")).thenReturn(Optional.empty());

        TaskPatchCommand cmd = TaskPatchCommand.builder()
                .taskId(42L)
                .actor("actor")
                .statusPresent(false)
                .titlePresent(false)
                .descriptionPresent(false)
                .priorityPresent(false)
                .tagsPresent(false)
                .userAffecteePresent(true)
                .userAffectee("ghost")
                .build();

        assertThatThrownBy(() -> taskService.patchTask(cmd))
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessageContaining("ghost");

        verify(taskRepository, never()).save(any());
        verifyNoInteractions(outboxService);
    }

    @Test
    void patchTask_shouldClearUserAndEmitEvent_whenUserPresentAndNull() {
        when(taskRepository.findById(42L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        TaskPatchCommand cmd = TaskPatchCommand.builder()
                .taskId(42L)
                .actor("actor")
                .statusPresent(false)
                .titlePresent(false)
                .descriptionPresent(false)
                .priorityPresent(false)
                .tagsPresent(false)
                .userAffecteePresent(true)
                .userAffectee(null)
                .build();

        Task result = taskService.patchTask(cmd);

        assertThat(result.getAccount()).isNull();
        verify(outboxService).ticketChangedUserAffecteeAuditEvent(result, "oldUser", null, "actor");
        verify(outboxService, never()).ticketStatusChangedAuditEvent(any(), any(), any(), any());
    }

    @Test
    void patchTask_shouldThrow_whenTaskNotFound() {
        when(taskRepository.findById(123L)).thenReturn(Optional.empty());

        TaskPatchCommand cmd = TaskPatchCommand.builder()
                .taskId(123L)
                .actor("actor")
                .statusPresent(true)
                .status(TaskStatus.DONE)
                .titlePresent(true)
                .title("t")
                .descriptionPresent(true)
                .description("d")
                .priorityPresent(true)
                .priority(3)
                .tagsPresent(true)
                .tagIds(List.of(1L))
                .userAffecteePresent(true)
                .userAffectee("userX")
                .build();

        assertThatThrownBy(() -> taskService.patchTask(cmd))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining("123");

        verify(taskRepository).findById(123L);
        verifyNoInteractions(outboxService);
    }
}