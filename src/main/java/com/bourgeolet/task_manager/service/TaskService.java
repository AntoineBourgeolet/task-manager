package com.bourgeolet.task_manager.service;


import com.bourgeolet.task_manager.dto.tag.TagDTO;
import com.bourgeolet.task_manager.dto.task.TaskCreateDTO;
import com.bourgeolet.task_manager.dto.task.TaskResponseDTO;
import com.bourgeolet.task_manager.entity.Task;
import com.bourgeolet.task_manager.entity.User;
import com.bourgeolet.task_manager.exception.user.TaskNotFoundException;
import com.bourgeolet.task_manager.exception.user.UserNotFoundException;
import com.bourgeolet.task_manager.mapper.TaskMapper;
import com.bourgeolet.task_manager.model.task.TaskStatus;
import com.bourgeolet.task_manager.repository.TaskRepository;
import com.bourgeolet.task_manager.repository.UserRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    private final TaskMapper taskMapper;

    private final OutboxService outboxService;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository,  TaskMapper taskMapper, OutboxService outboxService) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.taskMapper = taskMapper;
        this.outboxService = outboxService;
    }


    public TaskResponseDTO create(TaskCreateDTO dto) {
        Task task = taskMapper.taskFromTaskCreateDTO(dto);
        return taskMapper.taskToTaskResponseDTO(taskRepository.save(task));
    }


    public Boolean existById(Long idTask) {
        return taskRepository.existsById(idTask);

    }

    public TaskResponseDTO getTaskById(Long idTask) {
        return taskMapper.taskToTaskResponseDTO(taskRepository.findById(idTask).orElseThrow(() -> new TaskNotFoundException(idTask)));

    }

    public void deleteTask(Long idTask) {
        Task task = taskRepository.findById(idTask).orElseThrow(() -> new TaskNotFoundException(idTask));
        taskRepository.delete(task);
    }

    public List<TaskResponseDTO> findAll() {
        List<Task> taskList = taskRepository.findAll();

        return taskList.stream()
                .map(this::toTaskResponseDTO)// TODO : corriger avec le déplacement du mapper ++ corriger les test
                .toList();
    }

    public List<TaskResponseDTO> getTasksByUserId(String username) throws UserNotFoundException {
        if (userRepository.findUserByUsername(username) != null) {
            throw new UserNotFoundException(username);
        }

        List<Task> taskList = taskRepository.findByUser(username);

        return taskList.stream()
                .map(this::toTaskResponseDTO)// TODO : corriger avec le déplacement du mapper
                .toList();
    }


    public TaskResponseDTO changeStatus(Long idTask, TaskStatus newStatus) {
        Task task = taskRepository.findById(idTask).orElseThrow(() -> new TaskNotFoundException(idTask));
        TaskStatus oldStatus = task.getStatus();
        task.setStatus(newStatus);
        TaskResponseDTO result = taskMapper.taskToTaskResponseDTO(taskRepository.save(task));
        outboxService.statusChangedAuditEvent(result,oldStatus.name(), newStatus.name());
        return result;

    }

    public TaskResponseDTO changeUser(Long idTask, String username) {
        Task newTask = taskRepository.findById(idTask).orElseThrow(() -> new TaskNotFoundException(idTask));

        if (username.equals("undefined")) {
            newTask.setUser(null);
        } else {
            User newUser = userRepository.findUserByUsername(username);
            if (newUser == null) {
                throw new UserNotFoundException(null);
            }
            newTask.setUser(newUser);
        }

        return taskMapper.taskToTaskResponseDTO(taskRepository.save(newTask));

    }



}
