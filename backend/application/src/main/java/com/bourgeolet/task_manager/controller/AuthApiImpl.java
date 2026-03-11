package com.bourgeolet.task_manager.controller;

import com.bourgeolet.task_manager.api.auth.AuthApi;
import com.bourgeolet.task_manager.dto.auth.AuthResponseDTO;
import com.bourgeolet.task_manager.dto.auth.LoginRequestDTO;
import com.bourgeolet.task_manager.dto.auth.RefreshTokenRequestDTO;
import com.bourgeolet.task_manager.dto.auth.RegisterRequestDTO;
import com.bourgeolet.task_manager.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class AuthApiImpl implements AuthApi {

    private final AuthService authService;

    @Override
    public ResponseEntity<@NotNull AuthResponseDTO> login(LoginRequestDTO loginRequestDTO) {
        return ResponseEntity.ok(authService.login(loginRequestDTO));
    }

    @Override
    public ResponseEntity<@NotNull AuthResponseDTO> register(RegisterRequestDTO registerRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(registerRequestDTO));
    }

    @Override
    public ResponseEntity<@NotNull AuthResponseDTO> refreshToken(RefreshTokenRequestDTO refreshTokenRequestDTO) {
        return ResponseEntity.ok(authService.refreshToken(refreshTokenRequestDTO));
    }
}
