package com.bourgeolet.task_manager.mapper;

import com.bourgeolet.task_manager.dto.tag.TagCreateDTO;
import com.bourgeolet.task_manager.dto.task.TaskCreateDTO;
import com.bourgeolet.task_manager.dto.task.TaskResponseDTO;
import com.bourgeolet.task_manager.entity.Task;
import com.bourgeolet.task_manager.entity.User;
import com.bourgeolet.task_manager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TaskMapper {

    private final UserService userService;

    public static List<String> tagToString(List<TagCreateDTO> listTag) {
        return listTag.stream().map(TagCreateDTO::name).toList();
    }

    public @NotNull Task taskFromTaskCreateDTO(TaskCreateDTO dto) {
        User user = userService.getUserByUsername(dto.userAffectee());
        Task tasks = new Task();
        tasks.setUser(user);
        tasks.setTitle(dto.title());
        tasks.setDescription(dto.description());
        tasks.setTags(tagToString(dto.tags()));
        tasks.setPriority(dto.priority());
        return tasks;
    }

    public TaskResponseDTO taskToTaskResponseDTO(Task tasks) {

        final String username = (tasks.getUser() != null) ? tasks.getUser().getUsername() : null;

        return new TaskResponseDTO(tasks.getId(), tasks.getTitle(), tasks.getDescription(), username, tasks.getPriority(), tasks.getTags(), tasks.getStatus());
    }
}
