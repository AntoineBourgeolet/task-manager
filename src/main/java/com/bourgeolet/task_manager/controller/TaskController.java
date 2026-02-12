package com.bourgeolet.task_manager.controller;

import com.bourgeolet.task_manager.dto.task.*;
import com.bourgeolet.task_manager.entity.Task;
import com.bourgeolet.task_manager.mapper.TaskMapper;
import com.bourgeolet.task_manager.model.task.TaskStatus;
import com.bourgeolet.task_manager.service.TaskService;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/task")
public class TaskController {

    private final TaskMapper taskMapper;

    private final TaskService taskService;


    public TaskController(TaskService taskService, TaskMapper taskMapper) {
        super();
        this.taskService = taskService;
        this.taskMapper = taskMapper;
    }

    private static List<TaskResponseDTO> getStatusList(List<TaskResponseDTO> listTask, TaskStatus taskStatus) {
        return listTask.stream().filter(taskResponseDTO -> taskResponseDTO.status().equals(taskStatus)).toList();
    }

    @PostMapping
    public ResponseEntity<@NotNull TaskResponseDTO> create(@Valid @RequestBody TaskCreateDTO dto) {
        Task result = taskService.create(taskMapper.taskFromTaskCreateDTO(dto), dto.actor());
        return ResponseEntity.accepted().body(taskMapper.taskToTaskResponseDTO(result));
    }

    @PatchMapping(path = "/modifyStatus")
    public ResponseEntity<@NotNull TaskResponseDTO> modifyStatus(@Valid @RequestBody TaskChangeStatusDTO dto) {
        Task result = taskService.changeStatus(dto.id(), dto.newStatus(), dto.actor());
        return ResponseEntity.accepted().body(taskMapper.taskToTaskResponseDTO(result));
    }

    @PatchMapping(path = "/modifyUser")
    public ResponseEntity<@NotNull TaskResponseDTO> modifyUser(@Valid @RequestBody TaskChangeUserAffecteeDTO dto) {
        Task result = taskService.changeUserAffectee(dto.id(), dto.newUser(), dto.actor());
        return ResponseEntity.accepted().body(taskMapper.taskToTaskResponseDTO(result));
    }

    @DeleteMapping()
    public void delete(@Valid @RequestBody TaskDeleteDTO dto) {
        taskService.deleteTask(dto.id(), dto.actor());
    }

    @GetMapping
    public List<TaskResponseDTO> all() {
        return taskService.findAll();
    }

    @GetMapping(path = "/allByStatus")
    public TaskByStatusResponseDTO allByStatus() {
        List<TaskResponseDTO> listTask = taskService.findAll();
        return new TaskByStatusResponseDTO(getStatusList(listTask, TaskStatus.TODO), getStatusList(listTask, TaskStatus.BLOCKED), getStatusList(listTask, TaskStatus.DOING), getStatusList(listTask, TaskStatus.TESTING), getStatusList(listTask, TaskStatus.DONE));
    }

    @GetMapping(path = "/getTaskById/{taskId}")
    public TaskResponseDTO getTaskById(@PathVariable Long taskId) {
        return taskService.getTaskById(taskId);
    }


}
