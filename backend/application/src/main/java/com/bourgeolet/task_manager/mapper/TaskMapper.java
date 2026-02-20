package com.bourgeolet.task_manager.mapper;

import com.bourgeolet.task_manager.dto.task.TaskCreateDTO;
import com.bourgeolet.task_manager.dto.task.TaskResponseDTO;
import com.bourgeolet.task_manager.entity.Account;
import com.bourgeolet.task_manager.entity.Task;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TaskMapper {

    private final TagMapper tagMapper;

    public Task taskFromTaskCreateDTO(TaskCreateDTO dto) {
        Task task = new Task();
        if (!dto.getUserAffectee().isBlank()) {
            Account account = new Account();
            account.setUsername(dto.getUserAffectee());
            task.setAccount(account);
        }
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        if(dto.getTags().isEmpty()){
            task.setTags(dto.getTags().stream().map(tagMapper::tagCreateDTOToTag).toList());
        }
        task.setPriority(dto.getPriority());
        return task;

    }

    public TaskResponseDTO taskToTaskResponseDTO(Task task) {
        TaskResponseDTO taskResponseDTO = new TaskResponseDTO();
        taskResponseDTO.setDescription(task.getDescription());
        taskResponseDTO.setId(task.getId());
        taskResponseDTO.setPriority(task.getPriority());
        taskResponseDTO.setTags(task.getTags().stream().map(tagMapper::tagToTagResponseDTO).toList());
        taskResponseDTO.setTitle(task.getTitle());
        taskResponseDTO.setStatus(task.getStatus());
        if (task.getAccount() != null){
            taskResponseDTO.setUserAffectee(task.getAccount().getUsername());
        }
        return taskResponseDTO;
    }
}
