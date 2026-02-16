package com.bourgeolet.task_manager.service;


import com.bourgeolet.task_manager.entity.Account;
import com.bourgeolet.task_manager.entity.Task;
import com.bourgeolet.task_manager.exception.account.AccountNotFoundException;
import com.bourgeolet.task_manager.exception.task.TaskNotFoundException;
import com.bourgeolet.task_manager.model.task.TaskStatus;
import com.bourgeolet.task_manager.repository.AccountRepository;
import com.bourgeolet.task_manager.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final AccountRepository accountRepository;


    private final OutboxService outboxService;


    public Task create(Task task, String actor) {
        Task result = taskRepository.save(task);
        outboxService.ticketCreatedAuditEvent(result, actor);
        return result;
    }

    public Task changeStatus(Long idTask, TaskStatus newStatus, String actor) {
        Task tasks = taskRepository.findById(idTask).orElseThrow(() -> new TaskNotFoundException(idTask));
        TaskStatus oldStatus = tasks.getStatus();
        tasks.setStatus(newStatus);
        Task result = taskRepository.save(tasks);
        outboxService.ticketStatusChangedAuditEvent(result, oldStatus.name(), newStatus.name(), actor);
        return result;

    }

    public Task changeUserAffectee(Long idTask, String newUsername, String actor) {
        Task newTasks = taskRepository.findById(idTask).orElseThrow(() -> new TaskNotFoundException(idTask));
        String oldUsername = newTasks.getAccount() != null ? newTasks.getAccount().getUsername() : "";
        if (newUsername == null || newUsername.isBlank()) {
            newTasks.setAccount(null);
        } else {
            Account newAccount = accountRepository.findAccountByUsername(newUsername);
            if (newAccount == null) {
                throw new AccountNotFoundException(null);
            }
            newTasks.setAccount(newAccount);
        }

        Task result = taskRepository.save(newTasks);
        outboxService.ticketChangedUserAffecteeAuditEvent(result, oldUsername, newUsername, actor);

        return result;
    }

    public void deleteTask(Long idTask, String actor) {
        Task tasks = taskRepository.findById(idTask).orElseThrow(() -> new TaskNotFoundException(idTask));
        outboxService.ticketDeleteAuditEvent(idTask, actor);
        taskRepository.delete(tasks);
    }


    public List<Task> getTasksByUserId(String username) throws AccountNotFoundException {
        if (accountRepository.findAccountByUsername(username) != null) {
            throw new AccountNotFoundException(username);
        }

        return taskRepository.findByAccount(username);
    }

    public Task getTaskById(Long idTask) {
        return taskRepository.findById(idTask).orElseThrow(() -> new TaskNotFoundException(idTask));

    }

    public List<Task> findAll() {
        return taskRepository.findAll();
    }
}
