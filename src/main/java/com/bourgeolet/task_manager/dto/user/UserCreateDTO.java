package com.bourgeolet.task_manager.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserCreateDTO(
        @NotBlank String actor,

        @NotBlank String username,
        @Email String email
) {
}
