package com.bourgeolet.task_manager.dto;

import jakarta.validation.constraints.NotBlank;

public record TaskCreateDTO(
        Long idUser,
        @NotBlank String title,
        String description
) {
}
