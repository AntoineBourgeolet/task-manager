package com.bourgeolet.task_manager.service;

import com.bourgeolet.task_manager.dto.auth.AuthResponseDTO;
import com.bourgeolet.task_manager.dto.auth.LoginRequestDTO;
import com.bourgeolet.task_manager.dto.auth.RefreshTokenRequestDTO;
import com.bourgeolet.task_manager.dto.auth.RegisterRequestDTO;
import com.bourgeolet.task_manager.entity.Account;
import com.bourgeolet.task_manager.exception.auth.UsernameAlreadyExistsException;
import com.bourgeolet.task_manager.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_whenUsernameIsNew_shouldCreateAccountAndReturnTokens() {
        RegisterRequestDTO request = new RegisterRequestDTO();
        request.setUsername("antoine");
        request.setEmail("antoine@example.com");
        request.setPassword("password123");

        when(accountRepository.existsByUsername("antoine")).thenReturn(Optional.of(false));
        when(passwordEncoder.encode("password123")).thenReturn("encodedPwd");
        when(accountRepository.save(any(Account.class))).thenAnswer(inv -> {
            Account a = inv.getArgument(0);
            a.setId(1L);
            return a;
        });
        when(jwtService.generateAccessToken(any(UserDetails.class))).thenReturn("access-token");
        when(jwtService.generateRefreshToken(any(UserDetails.class))).thenReturn("refresh-token");

        AuthResponseDTO response = authService.register(request);

        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
        assertThat(response.getTokenType()).isEqualTo("Bearer");
        assertThat(response.getUsername()).isEqualTo("antoine");

        verify(accountRepository).existsByUsername("antoine");
        verify(passwordEncoder).encode("password123");
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void register_whenUsernameAlreadyExists_shouldThrowUsernameAlreadyExistsException() {
        RegisterRequestDTO request = new RegisterRequestDTO();
        request.setUsername("antoine");
        request.setPassword("password123");

        when(accountRepository.existsByUsername("antoine")).thenReturn(Optional.of(true));

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(UsernameAlreadyExistsException.class)
                .hasMessageContaining("antoine");

        verify(accountRepository).existsByUsername("antoine");
        verify(accountRepository, never()).save(any());
    }

    @Test
    void login_whenCredentialsAreValid_shouldReturnTokens() {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setUsername("antoine");
        request.setPassword("password123");

        Account account = new Account();
        account.setUsername("antoine");
        account.setPassword("encodedPwd");

        when(accountRepository.findAccountByUsername("antoine")).thenReturn(Optional.of(account));
        when(jwtService.generateAccessToken(any(UserDetails.class))).thenReturn("access-token");
        when(jwtService.generateRefreshToken(any(UserDetails.class))).thenReturn("refresh-token");

        AuthResponseDTO response = authService.login(request);

        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
        assertThat(response.getTokenType()).isEqualTo("Bearer");
        assertThat(response.getUsername()).isEqualTo("antoine");

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(accountRepository).findAccountByUsername("antoine");
    }

    @Test
    void login_whenAccountNotFound_shouldThrowBadCredentialsException() {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setUsername("unknown");
        request.setPassword("password");

        when(accountRepository.findAccountByUsername("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void refreshToken_whenTokenIsValid_shouldReturnNewTokens() {
        RefreshTokenRequestDTO request = new RefreshTokenRequestDTO();
        request.setRefreshToken("valid-refresh-token");

        Account account = new Account();
        account.setUsername("antoine");
        account.setPassword("encodedPwd");

        when(jwtService.extractUsername("valid-refresh-token")).thenReturn("antoine");
        when(accountRepository.findAccountByUsername("antoine")).thenReturn(Optional.of(account));
        when(jwtService.isTokenValid(anyString(), any(UserDetails.class))).thenReturn(true);
        when(jwtService.generateAccessToken(any(UserDetails.class))).thenReturn("new-access-token");
        when(jwtService.generateRefreshToken(any(UserDetails.class))).thenReturn("new-refresh-token");

        AuthResponseDTO response = authService.refreshToken(request);

        assertThat(response.getAccessToken()).isEqualTo("new-access-token");
        assertThat(response.getRefreshToken()).isEqualTo("new-refresh-token");
        assertThat(response.getUsername()).isEqualTo("antoine");
    }

    @Test
    void refreshToken_whenTokenIsInvalid_shouldThrowBadCredentialsException() {
        RefreshTokenRequestDTO request = new RefreshTokenRequestDTO();
        request.setRefreshToken("invalid-token");

        when(jwtService.extractUsername("invalid-token")).thenThrow(new RuntimeException("invalid"));

        assertThatThrownBy(() -> authService.refreshToken(request))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("Invalid refresh token");
    }

    @Test
    void refreshToken_whenTokenIsExpiredOrNotValid_shouldThrowBadCredentialsException() {
        RefreshTokenRequestDTO request = new RefreshTokenRequestDTO();
        request.setRefreshToken("expired-token");

        Account account = new Account();
        account.setUsername("antoine");
        account.setPassword("encodedPwd");

        when(jwtService.extractUsername("expired-token")).thenReturn("antoine");
        when(accountRepository.findAccountByUsername("antoine")).thenReturn(Optional.of(account));
        when(jwtService.isTokenValid(anyString(), any(UserDetails.class))).thenReturn(false);

        assertThatThrownBy(() -> authService.refreshToken(request))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("Invalid or expired refresh token");
    }
}
