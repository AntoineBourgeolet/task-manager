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
public class User {

    @OneToMany(mappedBy = "user", cascade = CascadeType.DETACH)
    private List<Task> idTicketAffectees;

    @NotBlank
    @Id
    private String username;

    private String email;


}
