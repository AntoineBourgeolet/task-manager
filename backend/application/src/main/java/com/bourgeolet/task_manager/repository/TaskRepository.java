package com.bourgeolet.task_manager.repository;

import com.bourgeolet.task_manager.entity.Task;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<@NotNull Task, @NotNull Long> {

    Optional<List<Task>> findByAccount(String username);

}
