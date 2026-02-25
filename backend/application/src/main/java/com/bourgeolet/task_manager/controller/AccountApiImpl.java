package com.bourgeolet.task_manager.controller;

import com.bourgeolet.task_manager.api.account.AccountApi;
import com.bourgeolet.task_manager.dto.account.AccountCreateDTO;
import com.bourgeolet.task_manager.dto.account.AccountResponseDTO;
import com.bourgeolet.task_manager.entity.Account;
import com.bourgeolet.task_manager.mapper.AccountMapper;
import com.bourgeolet.task_manager.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class AccountApiImpl implements AccountApi {

    private final AccountService accountService;
    private final AccountMapper accountMapper;


    @Override
    public ResponseEntity<@NotNull AccountResponseDTO> createAccount(AccountCreateDTO accountCreateDTO) {
        Account account = accountService.create(accountMapper.accountCreateDTOToAccount(accountCreateDTO));
        return ResponseEntity.accepted().body(accountMapper.accountToAccountResponseDTO(account));
    }

    @Override
    public ResponseEntity<@NotNull List<AccountResponseDTO>> getAllAccounts() {
        List<AccountResponseDTO> accountResponseDTOList = accountService.getAll().stream().map(accountMapper::accountToAccountResponseDTO).toList();
        return ResponseEntity.accepted().body(accountResponseDTOList);
    }
}