package com.bourgeolet.task_manager.mapper;

import com.bourgeolet.task_manager.dto.account.AccountCreateDTO;
import com.bourgeolet.task_manager.dto.account.AccountResponseDTO;
import com.bourgeolet.task_manager.entity.Account;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class AccountMapper {


    public AccountResponseDTO accountToAccountResponseDTO(Account account) {
        return new AccountResponseDTO(account.getId(), account.getUsername(), null);
    }

    public Account accountCreateDTOToAccount(@Valid AccountCreateDTO dto) {
        return Account.builder()
                .username(dto.getUsername())
                .email(dto.getEmail()).build();
    }
}
