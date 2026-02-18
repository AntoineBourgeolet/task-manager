package com.bourgeolet.task_manager.dto.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TaskChangeUserAffecteeDTO(
        @NotBlank String actor,

        @NotNull Long id,
        String newUser
) {
}
