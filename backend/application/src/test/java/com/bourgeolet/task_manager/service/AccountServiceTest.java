package com.bourgeolet.task_manager.service;

import com.bourgeolet.task_manager.entity.Account;
import com.bourgeolet.task_manager.exception.account.AccountNotFoundException;
import com.bourgeolet.task_manager.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    void create_whenUsernameIsValid_shouldSaveAndReturnAccount() {
        Account input = new Account();
        input.setUsername("antoine");

        Account saved = new Account();
        saved.setId(1L);
        saved.setUsername("antoine");

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        when(accountRepository.save(any(Account.class))).thenReturn(saved);

        Account result = accountService.create(input);

        assertThat(result).isEqualTo(saved);

        verify(accountRepository).save(captor.capture());
        Account passed = captor.getValue();
        assertThat(passed).isSameAs(input);
        assertThat(passed.getUsername()).isEqualTo("antoine");

        verifyNoMoreInteractions(accountRepository);
    }

    @Test
    void create_whenUsernameIsNull_shouldThrowIllegalArgumentException() {
        Account input = new Account();
        input.setUsername(null);

        assertThatThrownBy(() -> accountService.create(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("username is required");

        verifyNoInteractions(accountRepository);
    }

    @Test
    void create_whenUsernameIsBlank_shouldThrowIllegalArgumentException() {

        Account input = new Account();
        input.setUsername("   ");

        assertThatThrownBy(() -> accountService.create(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("username is required");

        verifyNoInteractions(accountRepository);
    }


    @Test
    void getAll_whenCalled_shouldDelegateToRepository() {
        Account a1 = new Account(); a1.setId(1L); a1.setUsername("u1");
        Account a2 = new Account(); a2.setId(2L); a2.setUsername("u2");
        when(accountRepository.findAll()).thenReturn(List.of(a1, a2));

        List<Account> result = accountService.getAll();

        assertThat(result).containsExactly(a1, a2);
        verify(accountRepository).findAll();
        verifyNoMoreInteractions(accountRepository);
    }



    @Test
    void getAccountByUsername_whenAccountExists_shouldReturnAccountFromRepository() {

        Account a = new Account(); a.setId(10L); a.setUsername("antoine");
        when(accountRepository.findAccountByUsername("antoine")).thenReturn(Optional.of(a));


        Account result = accountService.getAccountByUsername("antoine");


        assertThat(result).isEqualTo(a);
        verify(accountRepository).findAccountByUsername("antoine");
        verifyNoMoreInteractions(accountRepository);
    }

    @Test
    void getAccountByUsername_whenAccountDoesNotExist_shouldThrowAccountNotFoundException() {
        when(accountRepository.findAccountByUsername("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.getAccountByUsername("unknown"))
                .isInstanceOf(AccountNotFoundException.class)
                .hasStackTraceContaining("unknown");

        verify(accountRepository).findAccountByUsername("unknown");
        verifyNoMoreInteractions(accountRepository);
    }
}