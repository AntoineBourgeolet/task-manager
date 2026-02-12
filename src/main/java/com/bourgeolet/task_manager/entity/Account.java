package com.bourgeolet.task_manager.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "account")
@Getter
@Setter
public class Account {


    @OneToMany(mappedBy = "account", cascade = CascadeType.DETACH)
    private List<Task> idTicketAffectees;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_seq")
    @SequenceGenerator(name = "account_seq", sequenceName = "account_seq")
    private Long id;

    @NotBlank
    @Column(unique = true)
    private String username;

    private String email;


}
