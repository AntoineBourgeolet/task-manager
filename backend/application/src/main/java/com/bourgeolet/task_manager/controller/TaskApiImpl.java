package com.bourgeolet.task_manager.controller;

import com.bourgeolet.task_manager.api.task.TaskApi;
import com.bourgeolet.task_manager.command.TaskPatchCommand;
import com.bourgeolet.task_manager.dto.task.*;
import com.bourgeolet.task_manager.entity.Task;
import com.bourgeolet.task_manager.mapper.TaskMapper;
import com.bourgeolet.task_manager.service.TaskService;
import java.util.Collections;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

@RestController
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class TaskApiImpl implements TaskApi {

    private final TaskService taskService;
    private final TaskMapper taskMapper;
    private final JsonMapper jsonMapper;


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
    public ResponseEntity<@NotNull TaskResponseDTO> patchTask(Long id, String body) {

        JsonNode node = null;
        TaskPatchDTO dto = null;

            node = jsonMapper.readTree(body);
            dto = jsonMapper.readValue(body, TaskPatchDTO.class);

        Task updated = taskService.patchTask(
                TaskPatchCommand.builder()
                        .taskId(id)
                        .actor(dto.getActor())
                        .titlePresent(node.has("title"))
                        .title(dto.getTitle())
                        .priorityPresent(node.has("priority"))
                        .priority(dto.getPriority())
                        .descriptionPresent(node.has("description"))
                        .description(dto.getDescription())
                        .tagsPresent(node.has("tags"))
                        .tagIds(extractTagIds(dto.getTags()))
                        .statusPresent(node.has("status"))
                        .status(dto.getStatus())
                        .userAffecteePresent(node.has("userAffectee"))
                        .userAffectee(dto.getUserAffectee())
                        .build()
        );
        return ResponseEntity.accepted().body(taskMapper.taskToTaskResponseDTO(updated));
    }

    private List<Long> extractTagIds(List<?> tags) {
        if (tags == null || tags.isEmpty()) {
            return Collections.emptyList();
        }
        return tags.stream()
                .map(tag -> {
                    if (tag instanceof java.util.Map) {
                        Object idValue = ((java.util.Map<?, ?>) tag).get("id");
                        if (idValue != null) {
                            return Long.parseLong(idValue.toString());
                        }
                    }
                    return null;
                })
                .toList();
    }
}