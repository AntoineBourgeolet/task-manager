package com.bourgeolet.task_manager.dto;

public record TaskCreateDTO(
        Long idUser,
        String title,
        String description
) {
}
