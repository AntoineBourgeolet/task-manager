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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final AccountRepository accountRepository;
    private final TagRepository tagRepository;

    private final OutboxService outboxService;


    public Task create(Task task, String actor) {
        if (task.getAccount() != null && task.getAccount().getUsername() != null) {
            Account account = accountRepository.findAccountByUsername(task.getAccount().getUsername())
                    .orElseThrow(() -> new AccountNotFoundException(task.getAccount().getUsername()));
            task.setAccount(account);
        }
        Task result = taskRepository.save(task);
        outboxService.ticketCreatedAuditEvent(result, actor);
        return result;
    }

    public void deleteTask(Long idTask, String actor) {
        Task tasks = taskRepository.findById(idTask).orElseThrow(() -> new TaskNotFoundException(idTask));
        outboxService.ticketDeleteAuditEvent(idTask, actor);
        taskRepository.delete(tasks);
    }


    public List<Task>   getTasksByUserId(String username) throws AccountNotFoundException {
        accountRepository.findAccountByUsername(username)
                .orElseThrow(() -> new AccountNotFoundException(username));

        return taskRepository.findByAccount(username).orElse(Collections.emptyList());
    }

    public Task getTaskById(Long idTask) {
        return taskRepository.findById(idTask).orElseThrow(() -> new TaskNotFoundException(idTask));

    }

    public List<Task> findAll() {
        return taskRepository.findAll();
    }


    public Task patchTask(TaskPatchCommand cmd) {
        Task task = taskRepository.findById(cmd.taskId())
                .orElseThrow(() -> new TaskNotFoundException(cmd.taskId()));

        TaskStatus oldStatus = task.getStatus();
        String oldUsername = task.getAccount() != null ? task.getAccount().getUsername() : "";
        boolean statusChanged = false;
        boolean userChanged = false;

        if (cmd.statusPresent().equals(true)) {
            statusChanged = changeStatus(cmd, task, oldStatus);
        }

        if (cmd.userAffecteePresent().equals(true)) {
            userChanged = changeUserAffectee(cmd, task, oldUsername);
        }

        if (cmd.titlePresent().equals(true)) {
            task.setTitle(cmd.title());
        }

        if (cmd.descriptionPresent().equals(true)) {
            task.setDescription(cmd.description());
        }

        if (cmd.priorityPresent().equals(true)) {
            task.setPriority(cmd.priority());
        }

        if (cmd.tagsPresent().equals(true)) {
            changeTags(cmd, task);
        }

        Task result = taskRepository.save(task);

        if (statusChanged) {
            outboxService.ticketStatusChangedAuditEvent(result, oldStatus.name(), cmd.status().name(), cmd.actor());
        }

        if (userChanged) {
            outboxService.ticketChangedUserAffecteeAuditEvent(result, oldUsername, cmd.userAffectee(), cmd.actor());
        }

        return result;
    }

    private void changeTags(TaskPatchCommand cmd, Task task) {
        if (cmd.tagIds().isEmpty()) {
            task.setTags(Collections.emptyList());
        } else {
            List<Tag> tags = cmd.tagIds().stream()
                    .map(id -> tagRepository.findById(id).orElse(null))
                    .toList();
            task.setTags(tags);
        }
    }

    private boolean changeUserAffectee(TaskPatchCommand cmd, Task task, String oldUsername) {
        boolean userChanged;
        Account account = null;
        if (cmd.userAffectee() != null && !cmd.userAffectee().isBlank()) {
            account = accountRepository.findAccountByUsername(cmd.userAffectee())
                    .orElseThrow(() -> new AccountNotFoundException(cmd.userAffectee()));
        }
        task.setAccount(account);
        userChanged = !oldUsername.equals(cmd.userAffectee());
        return userChanged;
    }

    private static boolean changeStatus(TaskPatchCommand cmd, Task task, TaskStatus oldStatus) {
        boolean statusChanged;
        task.setStatus(cmd.status());
        statusChanged = !oldStatus.name().equals(cmd.status().name());
        return statusChanged;
    }
}
