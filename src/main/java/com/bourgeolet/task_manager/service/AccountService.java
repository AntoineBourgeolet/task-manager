package com.bourgeolet.task_manager.service;

import com.bourgeolet.task_manager.entity.Account;
import com.bourgeolet.task_manager.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public Account create(Account account) {
        return accountRepository.save(account);
    }

    public List<Account> findAll() {
        return accountRepository.findAll();

    }

    public Account getAccountByUsername(String username) {
        return accountRepository.findAccountByUsername(username);
    }

}
