package com.bourgeolet.task_manager.repository;

import com.bourgeolet.task_manager.entity.Account;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<@NotNull Account, @NotNull Long> {

    @Query("SELECT u FROM Account u where u.username = ?1")
    Optional<Account> findAccountByUsername(String username);

    Optional<Boolean> existsByUsername(String username);

}
