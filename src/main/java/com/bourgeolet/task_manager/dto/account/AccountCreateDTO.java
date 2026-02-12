package com.bourgeolet.task_manager.dto.account;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AccountCreateDTO(
        @NotBlank String actor,

        @NotBlank String username,
        @Email String email
) {
}
