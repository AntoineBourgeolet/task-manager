package com.bourgeolet.task_manager.dto.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TaskDeleteDTO(
        @NotBlank String actor,

        @NotNull Long id
) {
}
