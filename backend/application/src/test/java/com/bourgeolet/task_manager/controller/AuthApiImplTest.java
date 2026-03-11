package com.bourgeolet.task_manager.controller;

import com.bourgeolet.task_manager.dto.auth.AuthResponseDTO;
import com.bourgeolet.task_manager.dto.auth.LoginRequestDTO;
import com.bourgeolet.task_manager.dto.auth.RefreshTokenRequestDTO;
import com.bourgeolet.task_manager.dto.auth.RegisterRequestDTO;
import com.bourgeolet.task_manager.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthApiImplTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthApiImpl authApi;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void login_whenValidCredentials_shouldReturnOkAndAuthResponse() {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setUsername("antoine");
        request.setPassword("password123");

        AuthResponseDTO authResponse = AuthResponseDTO.builder()
                .accessToken("access-token")
                .refreshToken("refresh-token")
                .tokenType("Bearer")
                .username("antoine")
                .build();

        when(authService.login(request)).thenReturn(authResponse);

        ResponseEntity<AuthResponseDTO> response = authApi.login(request);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(authResponse);
        verify(authService).login(request);
    }

    @Test
    void register_whenValidData_shouldReturnCreatedAndAuthResponse() {
        RegisterRequestDTO request = new RegisterRequestDTO();
        request.setUsername("antoine");
        request.setEmail("antoine@example.com");
        request.setPassword("password123");

        AuthResponseDTO authResponse = AuthResponseDTO.builder()
                .accessToken("access-token")
                .refreshToken("refresh-token")
                .tokenType("Bearer")
                .username("antoine")
                .build();

        when(authService.register(request)).thenReturn(authResponse);

        ResponseEntity<AuthResponseDTO> response = authApi.register(request);

        assertThat(response.getStatusCode().value()).isEqualTo(201);
        assertThat(response.getBody()).isEqualTo(authResponse);
        verify(authService).register(request);
    }

    @Test
    void refreshToken_whenValidRefreshToken_shouldReturnOkAndAuthResponse() {
        RefreshTokenRequestDTO request = new RefreshTokenRequestDTO();
        request.setRefreshToken("valid-refresh-token");

        AuthResponseDTO authResponse = AuthResponseDTO.builder()
                .accessToken("new-access-token")
                .refreshToken("new-refresh-token")
                .tokenType("Bearer")
                .username("antoine")
                .build();

        when(authService.refreshToken(request)).thenReturn(authResponse);

        ResponseEntity<AuthResponseDTO> response = authApi.refreshToken(request);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(authResponse);
        verify(authService).refreshToken(request);
    }
}
