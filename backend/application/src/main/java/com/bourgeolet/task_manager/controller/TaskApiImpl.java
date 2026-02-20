package com.bourgeolet.task_manager.controller;

import com.bourgeolet.task_manager.api.task.TaskApi;
import com.bourgeolet.task_manager.dto.task.*;
import com.bourgeolet.task_manager.entity.Task;
import com.bourgeolet.task_manager.mapper.TaskMapper;
import com.bourgeolet.task_manager.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class TaskApiImpl implements TaskApi {

    private final TaskService taskService;
    private final TaskMapper taskMapper;

    private static List<TaskResponseDTO> filterByStatus(List<TaskResponseDTO> list, TaskStatus status) {
        return list.stream().filter(t -> t.getStatus() == status).toList();
    }

    @Override
    public ResponseEntity<@NotNull TaskResponseDTO> createTask(TaskCreateDTO dto) {
        Task created = taskService.create(taskMapper.taskFromTaskCreateDTO(dto), dto.getActor());
        return ResponseEntity.accepted().body(taskMapper.taskToTaskResponseDTO(created));
    }

    @Override
    public ResponseEntity<@NotNull Void> deleteTask(TaskDeleteDTO dto) {
        taskService.deleteTask(dto.getId(), dto.getActor());
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<@NotNull List<TaskResponseDTO>> listTasks() {
        List<TaskResponseDTO> list = taskService.findAll()
                .stream().map(taskMapper::taskToTaskResponseDTO)
                .toList();
        return ResponseEntity.ok(list);
    }

    @Override
    public ResponseEntity<@NotNull TaskByStatusResponseDTO> listTasksByStatus() {
        List<TaskResponseDTO> list = taskService.findAll()
                .stream().map(taskMapper::taskToTaskResponseDTO)
                .toList();

        return ResponseEntity.ok(
                new TaskByStatusResponseDTO(
                        filterByStatus(list, TaskStatus.TODO),
                        filterByStatus(list, TaskStatus.BLOCKED),
                        filterByStatus(list, TaskStatus.DOING),
                        filterByStatus(list, TaskStatus.TESTING),
                        filterByStatus(list, TaskStatus.DONE)
                )
        );
    }

    @Override
    public ResponseEntity<@NotNull TaskResponseDTO> getTaskById(Long taskId) {
        Task task = taskService.getTaskById(taskId);
        return ResponseEntity.ok(taskMapper.taskToTaskResponseDTO(task));
    }

    @Override
    public ResponseEntity<@NotNull TaskResponseDTO> modifyTaskStatus(TaskChangeStatusDTO dto) {
        Task updated = taskService.changeStatus(dto.getId(), dto.getNewStatus(), dto.getActor());
        return ResponseEntity.accepted().body(taskMapper.taskToTaskResponseDTO(updated));
    }

    @Override
    public ResponseEntity<@NotNull TaskResponseDTO> modifyTaskUser(TaskChangeUserAffecteeDTO dto) {
        Task updated = taskService.changeUserAffectee(dto.getId(), dto.getNewUser(), dto.getActor());
        return ResponseEntity.accepted().body(taskMapper.taskToTaskResponseDTO(updated));
    }
}