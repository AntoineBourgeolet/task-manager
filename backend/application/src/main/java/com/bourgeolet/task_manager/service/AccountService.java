package com.bourgeolet.task_manager.service;

import com.bourgeolet.task_manager.entity.Account;
import com.bourgeolet.task_manager.exception.account.AccountNotFoundException;
import com.bourgeolet.task_manager.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public Account create(Account account) {

        if (account.getUsername() == null || account.getUsername().isBlank()) {
            throw new IllegalArgumentException("username is required");
        }

        return accountRepository.save(account);
    }

    public List<Account> getAll() {
        return accountRepository.findAll();

    }

    public Account getAccountByUsername(String username) {
        return accountRepository.findAccountByUsername(username).orElseThrow(() -> new AccountNotFoundException(username));
    }

}
