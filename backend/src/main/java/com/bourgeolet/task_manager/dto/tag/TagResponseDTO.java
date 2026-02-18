package com.bourgeolet.task_manager.dto.tag;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TagResponseDTO(
        @NotNull Long id,
        @NotBlank String name
) {
}
