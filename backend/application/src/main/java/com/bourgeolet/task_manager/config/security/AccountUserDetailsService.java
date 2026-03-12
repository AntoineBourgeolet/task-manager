package com.bourgeolet.task_manager.config.security;

import com.bourgeolet.task_manager.entity.Account;
import com.bourgeolet.task_manager.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountUserDetailsService implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Override
    public @NonNull UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        Account account = accountRepository.findAccountByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Account not found for username: " + username));

        return User.builder()
            .username(account.getUsername())
            .password(account.getPasswordHash())
            .authorities("ROLE_" + account.getRole().name())
            .disabled(!Boolean.TRUE.equals(account.getEnabled()))
            .build();
    }
}



