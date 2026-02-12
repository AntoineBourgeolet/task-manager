package com.bourgeolet.task_manager.dto.task;

import com.bourgeolet.task_manager.dto.tag.TagDTO;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record TaskCreateDTO(
        @NotBlank String actor,

        String userAffectee,
        @NotBlank String title,
        String description,
        int priority,
        List<TagDTO> tags
) {
}
