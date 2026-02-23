package com.bourgeolet.task_manager.entity;

import com.bourgeolet.task_manager.repository.AccountRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@ActiveProfiles("test")
@DataJpaTest
class AccountJpaTest {

    @Autowired
    private AccountRepository accountRepository;

    private Validator validator;

    @BeforeEach
    void initValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void persist_shouldGenerateId_whenAccountIsValid() {

        Account acc = new Account();
        acc.setUsername("antoine");
        acc.setEmail("x@y.z");


        Account saved = accountRepository.save(acc);


        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUsername()).isEqualTo("antoine");
        assertThat(saved.getEmail()).isEqualTo("x@y.z");
    }

    @Test
    void validation_shouldFail_whenUsernameIsNull() {

        Account acc = new Account();
        acc.setUsername(null);
        acc.setEmail("x@y.z");


        Set<ConstraintViolation<Account>> violations = validator.validate(acc);


        assertThat(violations)
                .anySatisfy(v -> {
                    assertThat(v.getPropertyPath()).hasToString("username");
                    assertThat(v.getMessage()).isNotBlank();
                });
    }

    @Test
    void validation_shouldFail_whenUsernameIsBlank() {

        Account acc = new Account();
        acc.setUsername("   ");
        acc.setEmail("x@y.z");


        Set<ConstraintViolation<Account>> violations = validator.validate(acc);


        assertThat(violations)
                .anySatisfy(v -> {
                    assertThat(v.getPropertyPath()).hasToString("username");
                    assertThat(v.getMessage()).isNotBlank();
                });
    }

    @Test
    void uniqueConstraint_shouldThrow_whenTwoAccountsUseSameUsername() {

        Account a1 = new Account();
        a1.setUsername("antoine");
        a1.setEmail("a1@test");

        Account a2 = new Account();
        a2.setUsername("antoine");
        a2.setEmail("a2@test");

        accountRepository.saveAndFlush(a1);


        assertThatThrownBy(() -> {
            accountRepository.saveAndFlush(a2);
        }).isInstanceOf(DataIntegrityViolationException.class);
    }
}