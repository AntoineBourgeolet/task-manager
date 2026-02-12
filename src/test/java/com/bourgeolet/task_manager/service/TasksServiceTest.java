package com.bourgeolet.task_manager.service;

import com.bourgeolet.task_manager.dto.task.TaskCreateDTO;
import com.bourgeolet.task_manager.dto.task.TaskResponseDTO;
import com.bourgeolet.task_manager.entity.Tasks;
import com.bourgeolet.task_manager.entity.Users;
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
class TasksServiceTest {


    @Mock
    TaskRepository taskRepository;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    TaskService taskService;


    private Tasks tasksToSave;
    private TaskCreateDTO taskToSaveDTO;
    private List<Tasks> listOfTasks;
    private Tasks simpleTasks1;
    private Tasks simpleTasks2;

    private Users users;

    @BeforeEach
    void setUp() {
        tasksToSave = new Tasks();
        tasksToSave.setTitle("Titre1");
        tasksToSave.setDescription("Description1");

        taskToSaveDTO = new TaskCreateDTO("actor","user","title","desc", 1, null);

        simpleTasks1 = new Tasks();
        simpleTasks1.setTitle("Titre1");
        simpleTasks1.setDescription("Description1");

        simpleTasks2 = new Tasks();
        simpleTasks2.setTitle("Titre2");
        simpleTasks2.setDescription("Description2");



        listOfTasks = new ArrayList<>();
        listOfTasks.add(simpleTasks1);
        listOfTasks.add(simpleTasks2);

        users = new Users();
        users.setUsername("Username");
        users.setEmail("email@email.com");
    }


    @Test
    void create_ok() {
        when(taskRepository.save(any(Tasks.class))).thenReturn(tasksToSave);

        TaskResponseDTO result = taskService.create(tasksToSave, "actor");

        assertNotNull(result);
        assertEquals("Titre1", result.title());
        assertEquals("Description1", result.description());

        verify(taskRepository).save(tasksToSave);
    }


    @Test
    void findAll() {

        when(taskRepository.findAll()).thenReturn(listOfTasks);

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

        List<TaskResponseDTO> result = taskService.getTasksByUserId("Username");

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Titre1", result.get(0).title());
        assertEquals("Description1", result.get(0).description());
        assertEquals("Titre2", result.get(1).title());
        assertEquals("Description2", result.get(1).description());

        verify(userRepository).existsById(1L);
        verifyNoMoreInteractions(userRepository, taskRepository);

    }


}