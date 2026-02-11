package com.bourgeolet.task_manager.dto.task;

import com.bourgeolet.task_manager.model.task.TaskStatus;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record TaskResponseDTO(
        @NotBlank Long id,
        String title,
        String description,
        String userAffectee ,
         int priority,
         List<String>tags,
         TaskStatus status
) {

}
