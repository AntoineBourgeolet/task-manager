package com.bourgeolet.task_manager.controller;

import com.bourgeolet.task_manager.dto.TaskResponseDTO;
import com.bourgeolet.task_manager.dto.UserCreateDTO;
import com.bourgeolet.task_manager.dto.UserResponseDTO;
import com.bourgeolet.task_manager.entity.User;
import com.bourgeolet.task_manager.exception.UserNotFoundException;
import com.bourgeolet.task_manager.service.TaskService;
import com.bourgeolet.task_manager.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final TaskService taskService;


    public UserController(UserService userService, TaskService taskService) {
        super();
        this.userService = userService;
        this.taskService = taskService;
    }

    @PostMapping
    public UserResponseDTO create(@Valid @RequestBody UserCreateDTO dto) {
        User user = new User();
        user.setEmail(dto.email());
        user.setUsername(dto.username());
        return userService.create(user);
    }

    @GetMapping
    public List<UserResponseDTO> all() {
        return userService.findAll();
    }

    @GetMapping("/{user_id}/tasks")
    public List<TaskResponseDTO> findByUserId(@PathVariable Long user_id) throws UserNotFoundException {
        return taskService.getTasksByUserId(user_id);
    }

}
