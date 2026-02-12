package com.bourgeolet.task_manager.service;

import com.bourgeolet.task_manager.dto.account.AccountResponseDTO;
import com.bourgeolet.task_manager.entity.Account;
import com.bourgeolet.task_manager.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {


    @Mock
    AccountRepository accountRepository;

    @InjectMocks
    AccountService accountService;


    private Account accountToSave;
    private List<Account> listAccounts;
    private Account accountSimple1;


    @BeforeEach
    void setUp() {
        accountToSave = new Account();
        accountToSave.setUsername("Username");
        accountToSave.setEmail("email@email.com");

        accountSimple1 = new Account();
        accountSimple1.setUsername("username1");
        accountSimple1.setEmail("email1@email.com");

        Account accountSimple2 = new Account();
        accountSimple2.setUsername("username2");
        accountSimple2.setEmail("email2@email.com");

        listAccounts = new ArrayList<>();
        listAccounts.add(accountSimple1);
        listAccounts.add(accountSimple2);
    }

    @Test
    void create() {
        when(accountRepository.save(any(Account.class))).thenReturn(accountToSave);

        AccountResponseDTO response = accountService.create(accountToSave);

        assertNotNull(response);
        assertEquals("username", response.username());
        assertEquals("email@email.com", response.email());

        verify(accountRepository).save(accountToSave);
    }

    @Test
    void create_with_no_username() {

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> accountService.create(null)
        );
        assertEquals("username is required", ex.getMessage());

    }

    @Test
    void findAll() {
        when(accountRepository.findAll()).thenReturn(listAccounts);

        List<AccountResponseDTO> accountResponseDTOList = accountService.findAll();

        assertNotNull(accountResponseDTOList);
        assertEquals("username1", accountResponseDTOList.get(0).username());
        assertEquals("username2", accountResponseDTOList.get(1).username());
        assertEquals("email1@email.com", accountResponseDTOList.get(0).email());
        assertEquals("email2@email.com", accountResponseDTOList.get(1).email());

        verify(accountRepository).findAll();
    }


}