package com.bourgeolet.task_manager.repository;

import com.bourgeolet.task_manager.entity.Users;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<@NotNull Users, @NotNull Long> {

    @Query("SELECT u FROM User u where u.username = ?1")
    Users findUserByUsername(String username);

    Boolean existsByUsername(String username);

}
