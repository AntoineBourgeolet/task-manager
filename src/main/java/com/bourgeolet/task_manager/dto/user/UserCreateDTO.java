package com.bourgeolet.task_manager.dto.user;

import jakarta.validation.constraints.Email;

public record UserCreateDTO(
        String username,
        @Email String email
) {
}
