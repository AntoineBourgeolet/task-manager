package com.bourgeolet.task_manager.repository;

import com.bourgeolet.task_manager.entity.Tag;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<@NotNull Tag, @NotNull Long> {
}
