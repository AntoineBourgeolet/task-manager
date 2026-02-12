package com.bourgeolet.task_manager.service;


import com.bourgeolet.task_manager.dto.task.TaskResponseDTO;
import com.bourgeolet.task_manager.entity.Tasks;
import com.bourgeolet.task_manager.entity.Users;
import com.bourgeolet.task_manager.exception.user.TaskNotFoundException;
import com.bourgeolet.task_manager.exception.user.UserNotFoundException;
import com.bourgeolet.task_manager.mapper.TaskMapper;
import com.bourgeolet.task_manager.model.task.TaskStatus;
import com.bourgeolet.task_manager.repository.TaskRepository;
import com.bourgeolet.task_manager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    private final TaskMapper taskMapper;

    private final OutboxService outboxService;


    public TaskResponseDTO create(Tasks tasks, String actor) {
        TaskResponseDTO taskResponseDTO = taskMapper.taskToTaskResponseDTO(taskRepository.save(tasks));
        outboxService.ticketCreatedAuditEvent(taskResponseDTO, actor);
        return taskResponseDTO;
    }

    public TaskResponseDTO changeStatus(Long idTask, TaskStatus newStatus, String actor) {
        Tasks tasks = taskRepository.findById(idTask).orElseThrow(() -> new TaskNotFoundException(idTask));
        TaskStatus oldStatus = tasks.getStatus();
        tasks.setStatus(newStatus);
        TaskResponseDTO result = taskMapper.taskToTaskResponseDTO(taskRepository.save(tasks));
        outboxService.ticketStatusChangedAuditEvent(result, oldStatus.name(), newStatus.name(), actor);
        return result;

    }

    public TaskResponseDTO changeUser(Long idTask, String newUsername, String actor) {
        Tasks newTasks = taskRepository.findById(idTask).orElseThrow(() -> new TaskNotFoundException(idTask));
        String oldUsername = newTasks.getUsers() != null ? newTasks.getUsers().getUsername() : "";
        if (newUsername == null || newUsername.isBlank()) {
            newTasks.setUsers(null);
        } else {
            Users newUsers = userRepository.findUserByUsername(newUsername);
            if (newUsers == null) {
                throw new UserNotFoundException(null);
            }
            newTasks.setUsers(newUsers);
        }

        TaskResponseDTO taskResponseDTO = taskMapper.taskToTaskResponseDTO(taskRepository.save(newTasks));
        outboxService.ticketChangedUserAffecteeAuditEvent(taskResponseDTO, oldUsername, newUsername, actor);

        return taskResponseDTO;
    }

    public void deleteTask(Long idTask, String actor) {
        Tasks tasks = taskRepository.findById(idTask).orElseThrow(() -> new TaskNotFoundException(idTask));
        outboxService.ticketDeleteAuditEvent(idTask, actor);
        taskRepository.delete(tasks);
    }


    public List<TaskResponseDTO> getTasksByUserId(String username) throws UserNotFoundException {
        if (userRepository.findUserByUsername(username) != null) {
            throw new UserNotFoundException(username);
        }

        List<Tasks> tasksList = taskRepository.findByUser(username);

        return tasksList.stream().map(taskMapper::taskToTaskResponseDTO).toList();
    }

    public TaskResponseDTO getTaskById(Long idTask) {
        return taskMapper.taskToTaskResponseDTO(taskRepository.findById(idTask).orElseThrow(() -> new TaskNotFoundException(idTask)));

    }

    public List<TaskResponseDTO> findAll() {
        List<Tasks> tasksList = taskRepository.findAll();

        return tasksList.stream().map(taskMapper::taskToTaskResponseDTO).toList();
    }
}
