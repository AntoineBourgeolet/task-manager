package com.bourgeolet.task_manager.controller;

import com.bourgeolet.task_manager.dto.account.AccountCreateDTO;
import com.bourgeolet.task_manager.dto.account.AccountResponseDTO;
import com.bourgeolet.task_manager.entity.Account;
import com.bourgeolet.task_manager.mapper.AccountMapper;
import com.bourgeolet.task_manager.service.AccountService;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/account")
public class AccountController {

    private final AccountMapper accountMapper;

    private final AccountService accountService;


    public AccountController(AccountService accountService, AccountMapper accountMapper) {
        super();
        this.accountService = accountService;
        this.accountMapper = accountMapper;
    }

    @PostMapping
    public ResponseEntity<@NotNull AccountResponseDTO> create(@Valid @RequestBody AccountCreateDTO dto) {
        Account response = accountService.create(accountMapper.userCreateDTOToUser(dto));
        return ResponseEntity.accepted().body(accountMapper.userToUserResponseDTO(response));
    }

    @GetMapping
    public List<AccountResponseDTO> all() {
        return accountService.findAll().stream().map(accountMapper::userToUserResponseDTO).toList();
    }

}
