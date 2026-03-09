package com.bourgeolet.task_manager.mapper;

import com.bourgeolet.task_manager.dto.task.TaskCreateDTO;
import com.bourgeolet.task_manager.dto.task.TaskResponseDTO;
import com.bourgeolet.task_manager.entity.Account;
import com.bourgeolet.task_manager.entity.Tag;
import com.bourgeolet.task_manager.entity.Task;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TaskMapper {

    private final TagMapper tagMapper;

    public Task taskFromTaskCreateDTO(TaskCreateDTO dto) {
        Account account = null;
        List<Tag> listTags = null;
        if (!dto.getUserAffectee().isBlank()) {
            account = Account.builder()
                    .username(dto.getUserAffectee())
                    .build();
        }
        if(!dto.getTags().isEmpty()){
            listTags = dto.getTags().stream().map(tagMapper::tagCreateDTOToTag).toList();
        }
        return Task.builder()
                .account(account)
                .title(dto.getTitle())
                .description(dto.getDescription())
                .tags(listTags)
                .priority(dto.getPriority())
                .build();

    }

    public TaskResponseDTO taskToTaskResponseDTO(Task task) {
        String username = null;
        if (task.getAccount() != null){
            username = task.getAccount().getUsername();
        }
        return TaskResponseDTO.builder()
                .description(task.getDescription())
                .id(task.getId())
                .priority(task.getPriority())
                .tags(task.getTags().stream().map(tagMapper::tagToTagResponseDTO).toList())
                .title(task.getTitle())
                .status(task.getStatus())
                .userAffectee(username)
                .build();
    }
}
