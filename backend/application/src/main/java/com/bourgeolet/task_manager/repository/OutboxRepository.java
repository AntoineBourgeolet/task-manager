package com.bourgeolet.task_manager.repository;

import com.bourgeolet.task_manager.entity.Outbox;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface OutboxRepository extends JpaRepository<@NotNull Outbox, @NotNull Long> {


    @Query(value = """
        SELECT *
        FROM outbox
        WHERE published = false
        ORDER BY event_at
        LIMIT :batchSize
        """, nativeQuery = true)
    List<Outbox> getNextBatch(@Param("batchSize") int batchSize);

    @Modifying
    @Query(value = """
        UPDATE outbox
        SET published = true, published_at = now()
        WHERE id = :id
        """, nativeQuery = true)
    void markPublished(@Param("id") UUID id);


}
