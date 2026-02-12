package com.bourgeolet.task_manager.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "outbox")
public class Outbox {

    @Id
    private UUID id;

    private String aggregateType;

    private Long aggregateId;

    private String eventType;

    private LocalTime eventAt;

    @Column(columnDefinition = "TEXT")

    private String payload;

    private Boolean published;

    private LocalTime publishedAt;
}
