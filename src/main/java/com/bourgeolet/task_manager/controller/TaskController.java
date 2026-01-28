package com.bourgeolet.task_manager.controller;

import com.bourgeolet.task_manager.dto.TaskByStatusResponseDTO;
import com.bourgeolet.task_manager.dto.TaskCreateDTO;
import com.bourgeolet.task_manager.dto.TaskResponseDTO;
import com.bourgeolet.task_manager.entity.Task;
import com.bourgeolet.task_manager.model.TaskStatus;
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
        if (dto.userAffectee() != null) {
            task.setUser(userService.getUserByUsername(dto.userAffectee()));
        }
        task.setTitle(dto.title());
        task.setDescription(dto.description());
        task.setPriority(dto.priority());
        task.setTags(dto.tags());
        return taskService.create(task);
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

    @PatchMapping(path = "/modifyStatus/{task_id}")
    public TaskResponseDTO modifyStatus(@RequestParam("new_status") TaskStatus new_status, @PathVariable Long task_id) throws ClassNotFoundException {
        return taskService.changeStatus(task_id, new_status);
    }

    @PatchMapping(path = "/modifyUser/{task_id}")
    public TaskResponseDTO modifyUser(@RequestParam("new_user") String new_user, @PathVariable Long task_id) throws ClassNotFoundException {
        return taskService.changeUser(task_id, new_user);
    }

    private static List<TaskResponseDTO> getStatusList(List<TaskResponseDTO> listTask, TaskStatus taskStatus) {
        return listTask.stream().filter(taskResponseDTO -> taskResponseDTO.status().equals(taskStatus)).toList();
    }


}
