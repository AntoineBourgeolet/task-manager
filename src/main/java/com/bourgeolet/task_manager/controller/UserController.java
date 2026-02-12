package com.bourgeolet.task_manager.controller;

import com.bourgeolet.task_manager.dto.user.UserCreateDTO;
import com.bourgeolet.task_manager.dto.user.UserResponseDTO;
import com.bourgeolet.task_manager.mapper.UserMapper;
import com.bourgeolet.task_manager.service.UserService;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserMapper userMapper;

    private final UserService userService;


    public UserController(UserService userService, UserMapper userMapper) {
        super();
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PostMapping
    public ResponseEntity<@NotNull UserResponseDTO> create(@Valid @RequestBody UserCreateDTO dto) {
        UserResponseDTO response = userService.create(userMapper.userCreateDTOToUser(dto));
        return ResponseEntity.accepted().body(response);
    }

    @GetMapping
    public List<UserResponseDTO> all() {
        return userService.findAll();
    }

}
