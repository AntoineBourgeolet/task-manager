package com.bourgeolet.task_manager.entity;

import com.bourgeolet.task_manager.model.task.TaskStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "tags")
@Getter
@Setter
public class Tags {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "task_seq")
    @SequenceGenerator(name = "task_seq", sequenceName = "task_seq")
    private Long id;

    private String name;
}
