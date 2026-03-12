package com.bourgeolet.task_manager.config;

import com.bourgeolet.task_manager.entity.Account;
import com.bourgeolet.task_manager.entity.Role;
import com.bourgeolet.task_manager.repository.AccountRepository;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class DevDataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DevDataInitializer.class);

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_EMAIL    = "admin@dev.local";

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.initial-password}")
    private String adminInitialPassword;

    public DevDataInitializer(AccountRepository accountRepository,
                               PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.passwordEncoder   = passwordEncoder;
    }

    @Override
    public void run(@NonNull ApplicationArguments args) {
        boolean alreadyExists = accountRepository.existsByUsername(ADMIN_USERNAME).orElse(false);

        if (alreadyExists) {
            log.info("[DevSeed] Compte admin '{}' déjà présent — aucune action.", ADMIN_USERNAME);
            return;
        }

        Account admin = Account.builder()
            .username(ADMIN_USERNAME)
            .email(ADMIN_EMAIL)
            .passwordHash(passwordEncoder.encode(adminInitialPassword))
            .role(Role.ADMIN)
            .enabled(true)
            .build();

        accountRepository.save(admin);
        log.info("[DevSeed] Compte admin '{}' créé avec succès.", ADMIN_USERNAME);
    }
}

