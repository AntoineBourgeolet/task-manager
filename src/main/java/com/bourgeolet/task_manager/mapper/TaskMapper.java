package com.bourgeolet.task_manager.mapper;

import com.bourgeolet.task_manager.dto.tag.TagDTO;
import com.bourgeolet.task_manager.dto.task.TaskCreateDTO;
import com.bourgeolet.task_manager.dto.task.TaskResponseDTO;
import com.bourgeolet.task_manager.entity.Task;
import com.bourgeolet.task_manager.entity.User;
import com.bourgeolet.task_manager.service.UserService;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TaskMapper {

    UserService userService;

    public TaskMapper(UserService userService) {
        this.userService = userService;
    }

    public @NotNull Task taskFromTaskCreateDTO(TaskCreateDTO dto) {
        User user = userService.getUserByUsername(dto.userAffectee());
        Task task = new Task();
        task.setUser(user);
        task.setTitle(dto.title());
        task.setDescription(dto.description());
        task.setTags(tagToString(dto.tags()));
        task.setPriority(dto.priority());
        return task;
    }


    public static List<String> tagToString(List<TagDTO> listTag) {
        return listTag.stream().map(TagDTO::name).toList();
    }

    public TaskResponseDTO taskToTaskResponseDTO(Task task) {

        final String username = (task.getUser() != null) ? task.getUser().getUsername() : null;

        return new TaskResponseDTO(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                username,
                task.getPriority(),
                task.getTags(),
                task.getStatus()
        );
    }
}
