package com.bourgeolet.task_manager.controller;

import com.bourgeolet.task_manager.dto.account.AccountCreateDTO;
import com.bourgeolet.task_manager.dto.account.AccountResponseDTO;
import com.bourgeolet.task_manager.entity.Account;
import com.bourgeolet.task_manager.mapper.AccountMapper;
import com.bourgeolet.task_manager.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AccountApiImplTest {

    @Mock
    private AccountService accountService;

    @Mock
    private AccountMapper accountMapper;

    @InjectMocks
    private AccountApiImpl accountApi;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createAccount_shouldReturnAcceptedAndMappedDTO() {
        // Arrange
        AccountCreateDTO createDTO = new AccountCreateDTO();
        Account mappedAccount = new Account();
        Account savedAccount = new Account();
        AccountResponseDTO responseDTO = new AccountResponseDTO();

        when(accountMapper.accountCreateDTOToAccount(createDTO))
                .thenReturn(mappedAccount);

        when(accountService.create(mappedAccount))
                .thenReturn(savedAccount);

        when(accountMapper.accountToAccountResponseDTO(savedAccount))
                .thenReturn(responseDTO);

        // Act
        ResponseEntity<AccountResponseDTO> response =
                accountApi.createAccount(createDTO);

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(202);
        assertThat(response.getBody()).isEqualTo(responseDTO);

        verify(accountMapper).accountCreateDTOToAccount(createDTO);
        verify(accountService).create(mappedAccount);
        verify(accountMapper).accountToAccountResponseDTO(savedAccount);
    }

    @Test
    void getAllAccounts_shouldReturnAcceptedAndListOfDTOs() {
        // Arrange
        Account account1 = new Account();
        Account account2 = new Account();

        AccountResponseDTO dto1 = new AccountResponseDTO();
        AccountResponseDTO dto2 = new AccountResponseDTO();

        when(accountService.getAll())
                .thenReturn(List.of(account1, account2));

        when(accountMapper.accountToAccountResponseDTO(account1))
                .thenReturn(dto1);

        when(accountMapper.accountToAccountResponseDTO(account2))
                .thenReturn(dto2);

        // Act
        ResponseEntity<List<AccountResponseDTO>> response =
                accountApi.getAllAccounts();

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(202);
        assertThat(response.getBody()).containsExactly(dto1, dto2);

        verify(accountService).getAll();
        verify(accountMapper).accountToAccountResponseDTO(account1);
        verify(accountMapper).accountToAccountResponseDTO(account2);
    }
}
