package com.bourgeolet.task_manager.service;

import com.bourgeolet.task_manager.dto.task.TaskCreateDTO;
import com.bourgeolet.task_manager.dto.task.TaskResponseDTO;
import com.bourgeolet.task_manager.entity.Account;
import com.bourgeolet.task_manager.entity.Task;
import com.bourgeolet.task_manager.mapper.TaskMapper;
import com.bourgeolet.task_manager.repository.AccountRepository;
import com.bourgeolet.task_manager.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class TaskServiceTest {


    @Mock
    TaskRepository taskRepository;

    @Mock
    AccountRepository accountRepository;

    @InjectMocks
    TaskService taskService;

    @Mock
    TaskMapper taskMapper;


    private Task tasksToSave;
    private TaskCreateDTO taskToSaveDTO;
    private List<Task> listOfTasks;
    private Task simpleTasks1;
    private Task simpleTasks2;

    private Account account;

    @BeforeEach
    void setUp() {
        tasksToSave = new Task();
        tasksToSave.setTitle("Titre1");
        tasksToSave.setDescription("Description1");

        taskToSaveDTO = new TaskCreateDTO("actor","user","title","desc", 1, null);

        simpleTasks1 = new Task();
        simpleTasks1.setTitle("Titre1");
        simpleTasks1.setDescription("Description1");

        simpleTasks2 = new Task();
        simpleTasks2.setTitle("Titre2");
        simpleTasks2.setDescription("Description2");



        listOfTasks = new ArrayList<>();
        listOfTasks.add(simpleTasks1);
        listOfTasks.add(simpleTasks2);

        account = new Account();
        account.setUsername("Username");
        account.setEmail("email@email.com");
    }


    @Test
    void create_ok() {
        when(taskRepository.save(any(Task.class))).thenReturn(tasksToSave);

        TaskResponseDTO result = taskMapper.taskToTaskResponseDTO(taskService.create(tasksToSave, "actor"));

        assertNotNull(result);
        assertEquals("Titre1", result.title());
        assertEquals("Description1", result.description());

        verify(taskRepository).save(tasksToSave);
    }


    @Test
    void findAll() {

        when(taskRepository.findAll()).thenReturn(listOfTasks);

        List<TaskResponseDTO> result = taskService.findAll().stream().map(taskMapper::taskToTaskResponseDTO).toList();

        assertNotNull(result);
        assertEquals("Titre1", result.get(0).title());
        assertEquals("Titre2", result.get(1).title());
        assertEquals("Description1", result.get(0).description());
        assertEquals("Description2", result.get(1).description());

        verify(taskRepository).findAll();

    }

    @Test
    void getTasksByUserId() {

        when(accountRepository.existsById(1L)).thenReturn(true);

        List<TaskResponseDTO> result = taskService.getTasksByUserId("Username").stream().map(taskMapper::taskToTaskResponseDTO).toList();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Titre1", result.get(0).title());
        assertEquals("Description1", result.get(0).description());
        assertEquals("Titre2", result.get(1).title());
        assertEquals("Description2", result.get(1).description());

        verify(accountRepository).existsById(1L);
        verifyNoMoreInteractions(accountRepository, taskRepository);

    }


}