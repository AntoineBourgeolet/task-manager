package com.bourgeolet.task_manager.service;

import com.bourgeolet.task_manager.dto.task.TaskStatus;
import com.bourgeolet.task_manager.entity.Account;
import com.bourgeolet.task_manager.entity.Task;
import com.bourgeolet.task_manager.exception.account.AccountNotFoundException;
import com.bourgeolet.task_manager.exception.task.TaskNotFoundException;
import com.bourgeolet.task_manager.repository.AccountRepository;
import com.bourgeolet.task_manager.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private OutboxService outboxService;

    @InjectMocks
    private TaskService taskService;


    @Test
    void create_shouldSaveAndEmitAuditEvent() {
        Task toSave = new Task();
        toSave.setStatus(TaskStatus.TODO);
        String actor = "antoine";

        Task saved = new Task();
        saved.setStatus(TaskStatus.TODO);

        when(taskRepository.save(toSave)).thenReturn(saved);

        Task result = taskService.create(toSave, actor);

        assertThat(result).isEqualTo(saved);
        verify(taskRepository).save(toSave);
        verify(outboxService).ticketCreatedAuditEvent(saved, actor);
        verifyNoMoreInteractions(taskRepository, outboxService);
        verifyNoInteractions(accountRepository);
    }

    @Test
    void changeStatus_shouldUpdateSaveAndEmitAuditEvent() {
        Long id = 10L;
        String actor = "alice";
        Task existing = new Task();
        existing.setStatus(TaskStatus.TODO);

        when(taskRepository.findById(id)).thenReturn(Optional.of(existing));

        ArgumentCaptor<Task> saveCaptor = ArgumentCaptor.forClass(Task.class);

        Task saved = new Task();
        saved.setStatus(TaskStatus.DONE);
        when(taskRepository.save(any(Task.class))).thenReturn(saved);

        Task result = taskService.changeStatus(id, TaskStatus.DONE, actor);

        assertThat(result.getStatus()).isEqualTo(TaskStatus.DONE);

        verify(taskRepository).findById(id);
        verify(taskRepository).save(saveCaptor.capture());
        Task updatedBeforeSave = saveCaptor.getValue();
        assertThat(updatedBeforeSave.getStatus()).isEqualTo(TaskStatus.DONE);

        verify(outboxService).ticketStatusChangedAuditEvent(saved, "TODO", "DONE", actor);
        verifyNoMoreInteractions(taskRepository, outboxService);
        verifyNoInteractions(accountRepository);
    }

    @Test
    void changeStatus_shouldThrow_whenTaskNotFound() {
        Long id = 404L;
        when(taskRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.changeStatus(id, TaskStatus.DOING, "actor"))
                .isInstanceOf(TaskNotFoundException.class);

        verify(taskRepository).findById(id);
        verifyNoMoreInteractions(taskRepository);
        verifyNoInteractions(outboxService, accountRepository);
    }


    @Test
    void changeUserAffectee_shouldDetachUser_whenNewUsernameIsNull() {
        Long id = 7L;
        String actor = "charlie";

        Account oldAcc = new Account();
        oldAcc.setUsername("oldUser");

        Task existing = new Task();
        existing.setAccount(oldAcc);

        when(taskRepository.findById(id)).thenReturn(Optional.of(existing));

        ArgumentCaptor<Task> saveCaptor = ArgumentCaptor.forClass(Task.class);

        Task saved = new Task();
        when(taskRepository.save(any(Task.class))).thenReturn(saved);

        Task result = taskService.changeUserAffectee(id, null, actor);

        assertThat(result).isEqualTo(saved);

        verify(taskRepository).findById(id);
        verify(taskRepository).save(saveCaptor.capture());
        Task updated = saveCaptor.getValue();
        assertThat(updated.getAccount()).isNull();

        verify(outboxService).ticketChangedUserAffecteeAuditEvent(saved, "oldUser", null, actor);
        verifyNoMoreInteractions(taskRepository, outboxService);
        verifyNoInteractions(accountRepository);
    }

    @Test
    void changeUserAffectee_shouldAttachUser_whenNewUsernameExists() {
        Long id = 8L;
        String actor = "dave";
        String newUsername = "newUser";

        Account oldAcc = new Account();
        oldAcc.setUsername("oldUser");

        Task existing = new Task();
        existing.setAccount(oldAcc);

        Account newAcc = new Account();
        newAcc.setUsername(newUsername);

        when(taskRepository.findById(id)).thenReturn(Optional.of(existing));
        when(accountRepository.findAccountByUsername(newUsername)).thenReturn(newAcc);

        ArgumentCaptor<Task> saveCaptor = ArgumentCaptor.forClass(Task.class);

        Task saved = new Task();
        when(taskRepository.save(any(Task.class))).thenReturn(saved);

        Task result = taskService.changeUserAffectee(id, newUsername, actor);

        assertThat(result).isEqualTo(saved);

        verify(taskRepository).findById(id);
        verify(accountRepository).findAccountByUsername(newUsername);
        verify(taskRepository).save(saveCaptor.capture());
        Task updated = saveCaptor.getValue();
        assertThat(updated.getAccount()).isSameAs(newAcc);

        verify(outboxService).ticketChangedUserAffecteeAuditEvent(saved, "oldUser", newUsername, actor);
        verifyNoMoreInteractions(taskRepository, outboxService, accountRepository);
    }

    @Test
    void changeUserAffectee_shouldThrow_whenNewUsernameUnknown() {
        Long id = 9L;
        String actor = "eric";
        String newUsername = "ghost";

        Task existing = new Task();
        when(taskRepository.findById(id)).thenReturn(Optional.of(existing));
        when(accountRepository.findAccountByUsername(newUsername)).thenReturn(null);

        assertThatThrownBy(() -> taskService.changeUserAffectee(id, newUsername, actor))
                .isInstanceOf(AccountNotFoundException.class);

        verify(taskRepository).findById(id);
        verify(accountRepository).findAccountByUsername(newUsername);
        verifyNoMoreInteractions(taskRepository, accountRepository);
        verifyNoInteractions(outboxService);
    }


    @Test
    void deleteTask_shouldEmitAuditAndDelete() {
        Long id = 33L;
        String actor = "zoe";
        Task existing = new Task();
        when(taskRepository.findById(id)).thenReturn(Optional.of(existing));

        taskService.deleteTask(id, actor);

        verify(taskRepository).findById(id);
        verify(outboxService).ticketDeleteAuditEvent(id, actor);
        verify(taskRepository).delete(existing);
        verifyNoMoreInteractions(taskRepository, outboxService);
        verifyNoInteractions(accountRepository);
    }

    @Test
    void deleteTask_shouldThrow_whenTaskNotFound() {
        Long id = 999L;
        when(taskRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.deleteTask(id, "actor"))
                .isInstanceOf(TaskNotFoundException.class);

        verify(taskRepository).findById(id);
        verifyNoMoreInteractions(taskRepository);
        verifyNoInteractions(outboxService, accountRepository);
    }


    @Test
    void getTasksByUserId_shouldThrow_whenAccountExists_perCurrentCode() {
        String username = "bob";
        when(accountRepository.findAccountByUsername(username)).thenReturn(new Account());

        assertThatThrownBy(() -> taskService.getTasksByUserId(username))
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessageContaining(username);

        verify(accountRepository).findAccountByUsername(username);
        verifyNoMoreInteractions(accountRepository);
        verifyNoInteractions(taskRepository, outboxService);
    }

    @Test
    void getTasksByUserId_shouldReturnTasks_whenAccountIsNull_perCurrentCode() {
        String username = "noone";
        when(accountRepository.findAccountByUsername(username)).thenReturn(null);

        Task t1 = new Task();
        Task t2 = new Task();
        when(taskRepository.findByAccount(username)).thenReturn(List.of(t1, t2));

        List<Task> result = taskService.getTasksByUserId(username);

        assertThat(result).containsExactly(t1, t2);

        verify(accountRepository).findAccountByUsername(username);
        verify(taskRepository).findByAccount(username);
        verifyNoMoreInteractions(accountRepository, taskRepository);
        verifyNoInteractions(outboxService);
    }


    @Test
    void getTaskById_shouldReturnTask() {
        Long id = 12L;
        Task t = new Task();
        when(taskRepository.findById(id)).thenReturn(Optional.of(t));

        Task result = taskService.getTaskById(id);

        assertThat(result).isSameAs(t);
        verify(taskRepository).findById(id);
        verifyNoMoreInteractions(taskRepository);
        verifyNoInteractions(accountRepository, outboxService);
    }

    @Test
    void getTaskById_shouldThrow_whenNotFound() {
        Long id = 13L;
        when(taskRepository.findById(id)).thenReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() -> taskService.getTaskById(id))
                .isInstanceOf(TaskNotFoundException.class);

        verify(taskRepository).findById(id);
        verifyNoMoreInteractions(taskRepository);
        verifyNoInteractions(accountRepository, outboxService);
    }

    @Test
    void findAll_shouldReturnList() {
        Task t1 = new Task();
        Task t2 = new Task();
        when(taskRepository.findAll()).thenReturn(List.of(t1, t2));

        List<Task> result = taskService.findAll();

        assertThat(result).containsExactly(t1, t2);
        verify(taskRepository).findAll();
        verifyNoMoreInteractions(taskRepository);
        verifyNoInteractions(accountRepository, outboxService);
    }
}