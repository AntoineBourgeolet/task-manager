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

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final AccountRepository accountRepository;
    private final TagRepository tagRepository;

    private final OutboxService outboxService;


    public Task create(Task task, String actor) {
        Task result = taskRepository.save(task);
        outboxService.ticketCreatedAuditEvent(result, actor);
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


    public Task patchTask(TaskPatchCommand cmd) {
        Task task = taskRepository.findById(cmd.taskId())
                .orElseThrow(() -> new TaskNotFoundException(cmd.taskId()));

        TaskStatus oldStatus = task.getStatus();
        String oldUsername = task.getAccount() != null ? task.getAccount().getUsername() : "";
        boolean statusChanged = false;
        boolean userChanged = false;

        if (cmd.statusPresent().equals(true)) {
            task.setStatus(cmd.status());
            statusChanged = !oldStatus.name().equals(cmd.status().name());
        }

        if (cmd.userAffecteePresent().equals(true)) {
            Account account = null;
            if (cmd.userAffectee() != null) {
                account = accountRepository.findAccountByUsername(cmd.userAffectee());
                if (account == null) {
                    throw new AccountNotFoundException(cmd.userAffectee());
                }
            }
            task.setAccount(account);
            userChanged = !oldUsername.equals(cmd.userAffectee());
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
            List<Tag> tags = cmd.tagIds().stream()
                    .map(id -> tagRepository.findById(id).orElse(null))
                    .toList();
            task.setTags(tags);
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
}
