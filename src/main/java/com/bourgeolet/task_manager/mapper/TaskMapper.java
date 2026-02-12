package com.bourgeolet.task_manager.mapper;

import com.bourgeolet.task_manager.dto.tag.TagDTO;
import com.bourgeolet.task_manager.dto.task.TaskCreateDTO;
import com.bourgeolet.task_manager.dto.task.TaskResponseDTO;
import com.bourgeolet.task_manager.entity.Tasks;
import com.bourgeolet.task_manager.entity.Users;
import com.bourgeolet.task_manager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TaskMapper {

    private final UserService userService;

    public static List<String> tagToString(List<TagDTO> listTag) {
        return listTag.stream().map(TagDTO::name).toList();
    }

    public @NotNull Tasks taskFromTaskCreateDTO(TaskCreateDTO dto) {
        Users users = userService.getUserByUsername(dto.userAffectee());
        Tasks tasks = new Tasks();
        tasks.setUsers(users);
        tasks.setTitle(dto.title());
        tasks.setDescription(dto.description());
        tasks.setTags(tagToString(dto.tags()));
        tasks.setPriority(dto.priority());
        return tasks;
    }

    public TaskResponseDTO taskToTaskResponseDTO(Tasks tasks) {

        final String username = (tasks.getUsers() != null) ? tasks.getUsers().getUsername() : null;

        return new TaskResponseDTO(tasks.getId(), tasks.getTitle(), tasks.getDescription(), username, tasks.getPriority(), tasks.getTags(), tasks.getStatus());
    }
}
