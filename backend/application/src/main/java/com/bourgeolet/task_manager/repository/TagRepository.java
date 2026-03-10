package com.bourgeolet.task_manager.repository;

import com.bourgeolet.task_manager.entity.Tag;
import jakarta.validation.constraints.NotBlank;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TagRepository extends JpaRepository<@NotNull Tag, @NotNull Long> {
    Optional<Tag> findByName(@NotBlank String name);
}
