package com.bourgeolet.task_manager.dto;

import jakarta.validation.constraints.NotBlank;

public record UserResponseDTO(
         @NotBlank String username,
        String email
) {

}
