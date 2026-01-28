package com.bourgeolet.task_manager.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record TaskByStatusResponseDTO(
        List<TaskResponseDTO> todo,
        List<TaskResponseDTO> blocked,
        List<TaskResponseDTO> doing,
        List<TaskResponseDTO> testing,
        List<TaskResponseDTO> done

) {

}
