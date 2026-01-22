package com.bourgeolet.task_manager.controller;

import com.bourgeolet.task_manager.dto.TaskCreateDTO;
import com.bourgeolet.task_manager.dto.TaskResponseDTO;
import com.bourgeolet.task_manager.entity.Task;
import com.bourgeolet.task_manager.service.TaskService;
import com.bourgeolet.task_manager.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;
    private final UserService userService;


    public TaskController(TaskService taskService, UserService userService) {
        super();
        this.taskService = taskService;
        this.userService = userService;
    }

    @PostMapping
    public TaskResponseDTO create(@Valid @RequestBody TaskCreateDTO dto) {
        Task task = new Task();
        if (dto.idUser() != null) {
            task.setUser(userService.getUserById(dto.idUser()));
        }
        task.setTitle(dto.title());
        task.setDescription(dto.description());
        return taskService.create(task);
    }

    @GetMapping
    public List<TaskResponseDTO> all() {
        return taskService.findAll();

    }


}
