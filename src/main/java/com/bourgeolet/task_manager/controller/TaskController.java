package com.bourgeolet.task_manager.controller;

import com.bourgeolet.task_manager.dto.task.TaskByStatusResponseDTO;
import com.bourgeolet.task_manager.dto.task.TaskCreateDTO;
import com.bourgeolet.task_manager.dto.task.TaskResponseDTO;
import com.bourgeolet.task_manager.model.task.TaskStatus;
import com.bourgeolet.task_manager.service.TaskService;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;


    public TaskController(TaskService taskService) {
        super();
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<@NotNull TaskResponseDTO> create(@Valid @RequestBody TaskCreateDTO dto) {
        return ResponseEntity.accepted().body(taskService.create(dto));
    }


    @GetMapping
    public List<TaskResponseDTO> all() {
        return taskService.findAll();
    }

    @GetMapping(path = "/allByStatus")
    public TaskByStatusResponseDTO allByStatus() {
        List<TaskResponseDTO> listTask = taskService.findAll();
        return new TaskByStatusResponseDTO(
                getStatusList(listTask, TaskStatus.TODO),
                getStatusList(listTask, TaskStatus.BLOCKED),
                getStatusList(listTask, TaskStatus.DOING),
                getStatusList(listTask, TaskStatus.TESTING),
                getStatusList(listTask, TaskStatus.DONE)
        );
    }

    @PatchMapping(path = "/modifyStatus/{taskId}")
    public TaskResponseDTO modifyStatus(@RequestParam("newStatus") TaskStatus newStatus, @PathVariable Long taskId) {
        return taskService.changeStatus(taskId, newStatus);
    }

    @PatchMapping(path = "/modifyUser/{taskId}")
    public TaskResponseDTO modifyUser(@RequestParam("newUser") String newUser, @PathVariable Long taskId) {
        return taskService.changeUser(taskId, newUser);
    }

    @DeleteMapping(path = "/{taskId}")
    public void deleteTask(@PathVariable Long taskId) {
        taskService.deleteTask(taskId);
    }

    @GetMapping(path = "/getTaskById/{taskId}")
    public TaskResponseDTO getTaskById(@PathVariable Long taskId) {
        return taskService.getTaskById(taskId);
    }

    private static List<TaskResponseDTO> getStatusList(List<TaskResponseDTO> listTask, TaskStatus taskStatus) {
        return listTask.stream().filter(taskResponseDTO -> taskResponseDTO.status().equals(taskStatus)).toList();
    }


}
