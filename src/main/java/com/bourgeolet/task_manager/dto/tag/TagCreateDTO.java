package com.bourgeolet.task_manager.dto.tag;

import jakarta.validation.constraints.NotBlank;

public record TagCreateDTO(
        @NotBlank String name
) {
}
