package com.bourgeolet.task_manager.dto.account;

import jakarta.validation.constraints.NotBlank;

public record AccountResponseDTO(
         @NotBlank String username,
        String email
) {

}
