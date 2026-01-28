package com.bourgeolet.task_manager.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record TaskCreateDTO(
        String userAffectee,
        @NotBlank String title,
        String description,
        int priority,
        List<String> tags
) {
}
