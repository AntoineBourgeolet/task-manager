package com.bourgeolet.task_manager.dto.task;

import java.util.List;

public record TaskByStatusResponseDTO(
        List<TaskResponseDTO> TODO,
        List<TaskResponseDTO> BLOCKED,
        List<TaskResponseDTO> DOING,
        List<TaskResponseDTO> TESTING,
        List<TaskResponseDTO> DONE

) {

}
