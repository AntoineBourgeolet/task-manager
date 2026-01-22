package com.bourgeolet.task_manager.dto;

import jakarta.validation.constraints.NotBlank;

public record UserResponseDTO(
        @NotBlank Long id,
        String username,
        String email
) {

}
