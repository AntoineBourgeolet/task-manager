package com.bourgeolet.task_manager.service;


import com.bourgeolet.task_manager.dto.TaskResponseDTO;
import com.bourgeolet.task_manager.entity.Task;
import com.bourgeolet.task_manager.exception.UserNotFoundException;
import com.bourgeolet.task_manager.repository.TaskRepository;
import com.bourgeolet.task_manager.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }


    public TaskResponseDTO create(Task task) {
        if ((task.getTitle() == null)) {
            throw new IllegalArgumentException("title is required");
        }

        Task taskResponse = taskRepository.save(task);

        return toTaskResponseDTO(taskResponse);
    }

    public List<TaskResponseDTO> findAll() {
        List<Task> taskList = taskRepository.findAll();

        return taskList.stream()
                .map(this::toTaskResponseDTO)
                .toList();
    }

    public List<TaskResponseDTO> getTasksByUserId(Long userId) throws UserNotFoundException {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User " + userId + " not found");
        }

        List<Task> taskList = taskRepository.findByUserId(userId);

        return taskList.stream()
                .map(this::toTaskResponseDTO)
                .toList();
    }

    private TaskResponseDTO toTaskResponseDTO(Task task) {

        final String username = (task.getUser() != null) ? task.getUser().getUsername() : null;

        return new TaskResponseDTO(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                username,
                task.isDone()
        );
    }
}
