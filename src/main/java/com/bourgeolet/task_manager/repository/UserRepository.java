package com.bourgeolet.task_manager.repository;

import com.bourgeolet.task_manager.entity.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<@NotNull User, @NotNull Long> {

    @Query("SELECT u FROM User u where u.username = ?1")
    User findUserByUsername(String username);

    Boolean existsByUsername(String username);

}
