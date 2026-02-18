package com.bourgeolet.task_manager.entity;

import com.bourgeolet.task_manager.model.task.TaskStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "task")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "task_seq")
    @SequenceGenerator(name = "task_seq", sequenceName = "task_seq")
    private Long id;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "account_username")
    private Account account;

    private String title;

    private String description;

    private int priority;

    private List<String> tags;

    @Enumerated(EnumType.STRING)
    private TaskStatus status = TaskStatus.TODO;
}
