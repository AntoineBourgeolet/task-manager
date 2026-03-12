package com.bourgeolet.task_manager.service;

import com.bourgeolet.task_manager.dto.account.LoginRequestDTO;
import com.bourgeolet.task_manager.dto.account.LoginResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String TOKEN_TYPE = "Bearer";

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequestDTO.getUsername(), loginRequestDTO.getPassword())
        );

        JwtService.GeneratedJwt generatedJwt = jwtService.generateToken(authentication);

        return LoginResponseDTO.builder()
            .token(generatedJwt.token())
            .expiration(generatedJwt.expiration())
            .type(TOKEN_TYPE)
            .build();
    }
}

