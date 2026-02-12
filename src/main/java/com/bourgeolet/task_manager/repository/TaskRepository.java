package com.bourgeolet.task_manager.repository;

import com.bourgeolet.task_manager.entity.Task;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<@NotNull Task, @NotNull Long> {

    List<Task> findByUser(String username);

}
