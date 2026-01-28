package com.bourgeolet.task_manager.service;

import com.bourgeolet.task_manager.dto.TaskCreateDTO;
import com.bourgeolet.task_manager.dto.TaskResponseDTO;
import com.bourgeolet.task_manager.entity.Task;
import com.bourgeolet.task_manager.entity.User;
import com.bourgeolet.task_manager.repository.TaskRepository;
import com.bourgeolet.task_manager.repository.UserRepository;
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
    UserRepository userRepository;

    @InjectMocks
    TaskService taskService;


    private Task taskToSave;
    private TaskCreateDTO taskToSaveDTO;
    private List<Task> listOfTask;
    private Task simpleTask1;
    private Task simpleTask2;

    private User user;

    @BeforeEach
    void setUp() {
        taskToSave = new Task();
        taskToSave.setTitle("Titre1");
        taskToSave.setDescription("Description1");

        taskToSaveDTO = new TaskCreateDTO("Username","Titre1","Description1",1, null);

        simpleTask1 = new Task();
        simpleTask1.setTitle("Titre1");
        simpleTask1.setDescription("Description1");

        simpleTask2 = new Task();
        simpleTask2.setTitle("Titre2");
        simpleTask2.setDescription("Description2");



        listOfTask = new ArrayList<>();
        listOfTask.add(simpleTask1);
        listOfTask.add(simpleTask2);

        user = new User();
        user.setUsername("Username");
        user.setEmail("email@email.com");
    }


    @Test
    void create_ok() {
        when(taskRepository.save(any(Task.class))).thenReturn(taskToSave);

        TaskResponseDTO result = taskService.create(taskToSave);

        assertNotNull(result);
        assertEquals("Titre1", result.title());
        assertEquals("Description1", result.description());

        verify(taskRepository).save(taskToSave);
    }


    @Test
    void findAll() {

        when(taskRepository.findAll()).thenReturn(listOfTask);

        List<TaskResponseDTO> result = taskService.findAll();

        assertNotNull(result);
        assertEquals("Titre1", result.get(0).title());
        assertEquals("Titre2", result.get(1).title());
        assertEquals("Description1", result.get(0).description());
        assertEquals("Description2", result.get(1).description());

        verify(taskRepository).findAll();

    }

    @Test
    void getTasksByUserId() {

        when(userRepository.existsById(1L)).thenReturn(true);
        when(taskRepository.findByUsername("Username")).thenReturn(listOfTask);

        List<TaskResponseDTO> result = taskService.getTasksByUserId("Username");

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Titre1", result.get(0).title());
        assertEquals("Description1", result.get(0).description());
        assertEquals("Titre2", result.get(1).title());
        assertEquals("Description2", result.get(1).description());

        verify(userRepository).existsById(1L);
        verify(taskRepository).findByUsername("Username");
        verifyNoMoreInteractions(userRepository, taskRepository);

    }


}