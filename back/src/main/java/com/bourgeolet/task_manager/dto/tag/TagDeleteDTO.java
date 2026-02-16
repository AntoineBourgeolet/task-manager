package com.bourgeolet.task_manager.dto.tag;

import jakarta.validation.constraints.NotBlank;

public record TagDeleteDTO(
        @NotBlank String actor,

        @NotBlank Long id
) {
}
