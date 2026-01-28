package com.bourgeolet.task_manager.dto;

import com.bourgeolet.task_manager.entity.User;
import com.bourgeolet.task_manager.model.TaskStatus;
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
