package com.bourgeolet.task_manager.config;

import com.bourgeolet.task_manager.entity.Account;
import com.bourgeolet.task_manager.entity.Role;
import com.bourgeolet.task_manager.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DevDataInitializerTest {

    @Mock
    private AccountRepository accountRepository;

    private BCryptPasswordEncoder passwordEncoder;
    private DevDataInitializer initializer;

    private static void setField(Object target, String fieldName, Object value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("Impossible d'injecter le champ " + fieldName, e);
        }
    }

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        initializer = new DevDataInitializer(accountRepository, passwordEncoder);
        // injection du @Value via setter de test (champ package-private accessible via réflexion)
        setField(initializer, "adminInitialPassword", "test_secret");
    }

    @Test
    void run_whenAdminAlreadyExists_shouldNotSave() {
        when(accountRepository.existsByUsername("admin"))
                .thenReturn(Optional.of(true));

        initializer.run(null);

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void run_whenAdminAbsent_shouldSaveWithAdminRoleAndBcryptHash() {
        when(accountRepository.existsByUsername("admin"))
                .thenReturn(Optional.of(false));
        when(accountRepository.save(any(Account.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        initializer.run(null);

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(captor.capture());

        Account saved = captor.getValue();
        assertThat(saved.getUsername()).isEqualTo("admin");
        assertThat(saved.getEmail()).isEqualTo("admin@dev.local");
        assertThat(saved.getRole()).isEqualTo(Role.ADMIN);
        assertThat(saved.getEnabled()).isTrue();
        // Le hash doit correspondre au mot de passe initial
        assertThat(passwordEncoder.matches("test_secret", saved.getPasswordHash())).isTrue();
    }

    @Test
    void run_whenExistsByUsernameReturnsEmpty_shouldSaveAdmin() {
        when(accountRepository.existsByUsername("admin"))
                .thenReturn(Optional.empty());
        when(accountRepository.save(any(Account.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        initializer.run(null);

        verify(accountRepository).save(any(Account.class));
    }
}

