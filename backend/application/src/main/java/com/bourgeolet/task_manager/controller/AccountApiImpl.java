package com.bourgeolet.task_manager.controller;

import com.bourgeolet.task_manager.api.account.AccountApi;
import com.bourgeolet.task_manager.api.account.AuthApi;
import com.bourgeolet.task_manager.dto.account.AccountCreateDTO;
import com.bourgeolet.task_manager.dto.account.AccountResponseDTO;
import com.bourgeolet.task_manager.dto.account.LoginRequestDTO;
import com.bourgeolet.task_manager.dto.account.LoginResponseDTO;
import com.bourgeolet.task_manager.entity.Account;
import com.bourgeolet.task_manager.mapper.AccountMapper;
import com.bourgeolet.task_manager.service.AccountService;
import com.bourgeolet.task_manager.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.List;
import java.util.Optional;

@RestController
@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class AccountApiImpl implements AccountApi, AuthApi {

    private final AccountService accountService;
    private final AccountMapper accountMapper;
    private final AuthService authService;

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }


    @Override
    public ResponseEntity<@NotNull AccountResponseDTO> createAccount(AccountCreateDTO accountCreateDTO) {
        Account account = accountService.create(accountMapper.accountCreateDTOToAccount(accountCreateDTO));
        return ResponseEntity.accepted().body(accountMapper.accountToAccountResponseDTO(account));
    }

    @Override
    public ResponseEntity<@NotNull LoginResponseDTO> login(LoginRequestDTO loginRequestDTO) {
        try {
            return ResponseEntity.ok(authService.login(loginRequestDTO));
        } catch (AuthenticationException ignored) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @Override
    public ResponseEntity<@NotNull List<AccountResponseDTO>> getAllAccounts() {
        List<AccountResponseDTO> accountResponseDTOList = accountService.getAll().stream().map(accountMapper::accountToAccountResponseDTO).toList();
        return ResponseEntity.accepted().body(accountResponseDTOList);
    }
}