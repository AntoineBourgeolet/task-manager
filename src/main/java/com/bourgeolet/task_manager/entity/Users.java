package com.bourgeolet.task_manager.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
public class Users {


    @OneToMany(mappedBy = "user", cascade = CascadeType.DETACH)
    private List<Tasks> idTicketAffectees;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "task_seq")
    @SequenceGenerator(name = "task_seq", sequenceName = "task_seq")
    private Long id;

    @NotBlank
    @Column(unique = true)
    private String username;

    private String email;


}
