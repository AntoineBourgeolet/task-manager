package com.bourgeolet.task_manager.mapper;

import com.bourgeolet.task_manager.dto.tag.TagCreateDTO;
import com.bourgeolet.task_manager.dto.task.TaskCreateDTO;
import com.bourgeolet.task_manager.dto.task.TaskResponseDTO;
import com.bourgeolet.task_manager.entity.Account;
import com.bourgeolet.task_manager.entity.Task;
import com.bourgeolet.task_manager.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TaskMapper {

    private final AccountService accountService;

    public static List<String> tagToString(List<TagCreateDTO> listTag) {
        return listTag.stream().map(TagCreateDTO::name).toList();
    }

    public @NotNull Task taskFromTaskCreateDTO(TaskCreateDTO dto) {
        Account account = accountService.getAccountByUsername(dto.userAffectee());
        Task tasks = new Task();
        tasks.setAccount(account);
        tasks.setTitle(dto.title());
        tasks.setDescription(dto.description());
        tasks.setTags(tagToString(dto.tags()));
        tasks.setPriority(dto.priority());
        return tasks;
    }

    public TaskResponseDTO taskToTaskResponseDTO(Task tasks) {

        final String username = (tasks.getAccount() != null) ? tasks.getAccount().getUsername() : null;

        return new TaskResponseDTO(tasks.getId(), tasks.getTitle(), tasks.getDescription(), username, tasks.getPriority(), tasks.getTags(), tasks.getStatus());
    }
}
