package com.bourgeolet.task_manager.service;

import com.bourgeolet.task_manager.dto.auth.AuthResponseDTO;
import com.bourgeolet.task_manager.dto.auth.LoginRequestDTO;
import com.bourgeolet.task_manager.dto.auth.RefreshTokenRequestDTO;
import com.bourgeolet.task_manager.dto.auth.RegisterRequestDTO;
import com.bourgeolet.task_manager.entity.Account;
import com.bourgeolet.task_manager.exception.auth.UsernameAlreadyExistsException;
import com.bourgeolet.task_manager.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponseDTO register(RegisterRequestDTO request) {
        if (accountRepository.existsByUsername(request.getUsername()).orElse(false)) {
            throw new UsernameAlreadyExistsException(request.getUsername());
        }

        Account account = Account.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        accountRepository.save(account);

        UserDetails userDetails = buildUserDetails(account);
        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        return buildAuthResponse(accessToken, refreshToken, account.getUsername());
    }

    public AuthResponseDTO login(LoginRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        Account account = accountRepository.findAccountByUsername(request.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        UserDetails userDetails = buildUserDetails(account);
        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        return buildAuthResponse(accessToken, refreshToken, account.getUsername());
    }

    public AuthResponseDTO refreshToken(RefreshTokenRequestDTO request) {
        String token = request.getRefreshToken();
        String username;

        try {
            username = jwtService.extractUsername(token);
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid refresh token");
        }

        Account account = accountRepository.findAccountByUsername(username)
                .orElseThrow(() -> new BadCredentialsException("Invalid refresh token"));

        UserDetails userDetails = buildUserDetails(account);

        if (!jwtService.isTokenValid(token, userDetails)) {
            throw new BadCredentialsException("Invalid or expired refresh token");
        }

        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        return buildAuthResponse(accessToken, refreshToken, username);
    }

    private UserDetails buildUserDetails(Account account) {
        return User.builder()
                .username(account.getUsername())
                .password(account.getPassword() != null ? account.getPassword() : "")
                .roles("USER")
                .build();
    }

    private AuthResponseDTO buildAuthResponse(String accessToken, String refreshToken, String username) {
        return AuthResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .username(username)
                .build();
    }
}
