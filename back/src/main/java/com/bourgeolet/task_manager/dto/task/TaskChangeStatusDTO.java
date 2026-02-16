package com.bourgeolet.task_manager.dto.task;

import com.bourgeolet.task_manager.model.task.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TaskChangeStatusDTO(
        @NotBlank String actor,

        @NotNull Long id,
        @NotNull TaskStatus newStatus
) {
}
