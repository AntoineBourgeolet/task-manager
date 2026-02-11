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
@Table(name = "audit_log")
public class AuditLog {

    @Id
    private UUID id;

    private Long ticketId;

    private String eventType;

    private String who;


    @Column(columnDefinition = "TEXT")
    private String data;

    private LocalTime whenAt;
}
