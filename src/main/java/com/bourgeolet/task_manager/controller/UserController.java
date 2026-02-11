package com.bourgeolet.task_manager.controller;

import com.bourgeolet.task_manager.config.global.GlobalConstant;
import com.bourgeolet.task_manager.dto.user.UserCreateDTO;
import com.bourgeolet.task_manager.dto.user.UserResponseDTO;
import com.bourgeolet.task_manager.events.UserCreateCommand;
import com.bourgeolet.task_manager.kafka.user.UserCreateProducer;
import com.bourgeolet.task_manager.service.UserService;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.bourgeolet.task_manager.config.global.GlobalConstant.APPLICATION_KAFKA_SOURCE;
import static java.time.LocalTime.now;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserCreateProducer producer;

    public record CreateUserAcceptedDTO(String name, String status) {
    }


    public UserController(UserService userService, UserCreateProducer producer) {
        super();
        this.userService = userService;
        this.producer = producer;
    }

    @PostMapping
    public ResponseEntity<@NotNull UserResponseDTO> create(@Valid @RequestBody UserCreateDTO dto) {
        userService.create(dto);
        return ResponseEntity.accepted().body(response);
    }

    @GetMapping
    public List<UserResponseDTO> all() {
        return userService.findAll();
    }

}
