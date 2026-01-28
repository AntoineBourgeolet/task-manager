package com.bourgeolet.task_manager.service;


import com.bourgeolet.task_manager.dto.TaskResponseDTO;
import com.bourgeolet.task_manager.entity.Task;
import com.bourgeolet.task_manager.entity.User;
import com.bourgeolet.task_manager.exception.UserNotFoundException;
import com.bourgeolet.task_manager.model.TaskStatus;
import com.bourgeolet.task_manager.repository.TaskRepository;
import com.bourgeolet.task_manager.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }


    public TaskResponseDTO create(Task task) {

        Task taskResponse = taskRepository.save(task);

        return toTaskResponseDTO(taskResponse);
    }

    public List<TaskResponseDTO> findAll() {
        List<Task> taskList = taskRepository.findAll();

        return taskList.stream()
                .map(this::toTaskResponseDTO)
                .toList();
    }

    public List<TaskResponseDTO> getTasksByUserId(String username) throws UserNotFoundException {
        if (userRepository.findUserByUsername(username) != null) {
            throw new UserNotFoundException(username);
        }

        List<Task> taskList = taskRepository.findByUser(username);

        return taskList.stream()
                .map(this::toTaskResponseDTO)
                .toList();
    }


    public TaskResponseDTO changeStatus(Long taskId, TaskStatus newStatus) {
        Task newTask = taskRepository.findById(taskId).orElseThrow(() -> new EntityNotFoundException("Task not found"));
        newTask.setStatus(newStatus);
        return toTaskResponseDTO(taskRepository.save(newTask));

    }

    public TaskResponseDTO changeUser(Long taskId, String username) {
        Task newTask = taskRepository.findById(taskId).orElseThrow(() -> new EntityNotFoundException("Task not found"));
        User newUser = userRepository.findUserByUsername(username);
        if (newUser == null){
            throw new UserNotFoundException(null);
        }

        newTask.setUser(newUser);
        return toTaskResponseDTO(taskRepository.save(newTask));

    }

    private TaskResponseDTO toTaskResponseDTO(Task task) {

        final String username = (task.getUser() != null) ? task.getUser().getUsername() : null;

        return new TaskResponseDTO(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                username,
                task.getPriority(),
                task.getTags(),
                task.getStatus()
        );
    }

}
