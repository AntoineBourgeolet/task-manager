package com.bourgeolet.task_manager.dto;

import jakarta.validation.constraints.NotBlank;

public record TaskResponseDTO(
        @NotBlank Long id,
        String title,
        String description,
        String userAffectee,
        boolean done
) {

}
