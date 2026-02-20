package com.bourgeolet.task_manager.repository;

import com.bourgeolet.task_manager.entity.Account;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AccountRepository extends JpaRepository<@NotNull Account, @NotNull Long> {

    @Query("SELECT u FROM Account u where u.username = ?1")
    Account findAccountByUsername(String username);

    Boolean existsByUsername(String username);

}
