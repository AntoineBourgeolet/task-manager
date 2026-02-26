package com.bourgeolet.task_manager.controller;

import com.bourgeolet.task_manager.dto.task.*;
import com.bourgeolet.task_manager.entity.Task;
import com.bourgeolet.task_manager.mapper.TaskMapper;
import com.bourgeolet.task_manager.service.TaskService;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskApiImplTest {

    @Mock
    private TaskService taskService;

    @Mock
    private TaskMapper taskMapper;


    private TaskApiImpl taskApi;

    @BeforeEach
    void setUp() {
        JsonMapper jsonMapper = new JsonMapper();
        taskApi = new TaskApiImpl(taskService, taskMapper, jsonMapper);
    }

    @Test
    void createTask_shouldReturnAcceptedAndMappedDTO() {
        
        TaskCreateDTO createDTO = mock(TaskCreateDTO.class);
        when(createDTO.getActor()).thenReturn("antoine");

        Task mappedTask = new Task();
        Task createdTask = new Task();
        TaskResponseDTO responseDTO = new TaskResponseDTO();

        when(taskMapper.taskFromTaskCreateDTO(createDTO)).thenReturn(mappedTask);
        when(taskService.create(mappedTask, "antoine")).thenReturn(createdTask);
        when(taskMapper.taskToTaskResponseDTO(createdTask)).thenReturn(responseDTO);

        
        ResponseEntity<@NotNull TaskResponseDTO> response = taskApi.createTask(createDTO);

        
        assertThat(response.getStatusCode().value()).isEqualTo(202);
        assertThat(response.getBody()).isEqualTo(responseDTO);

        verify(taskMapper).taskFromTaskCreateDTO(createDTO);
        verify(taskService).create(mappedTask, "antoine");
        verify(taskMapper).taskToTaskResponseDTO(createdTask);
        verifyNoMoreInteractions(taskService, taskMapper);
    }

    @Test
    void deleteTask_shouldReturnNoContent() {
        
        TaskDeleteDTO deleteDTO = mock(TaskDeleteDTO.class);
        when(deleteDTO.getActor()).thenReturn("antoine");

        doNothing().when(taskService).deleteTask(42L, "antoine");

        
        ResponseEntity<@NotNull Void> response = taskApi.deleteTask(42L, deleteDTO);

        
        assertThat(response.getStatusCode().value()).isEqualTo(204);
        assertThat(response.getBody()).isNull();

        verify(taskService).deleteTask(42L, "antoine");
        verifyNoMoreInteractions(taskService);
        verifyNoInteractions(taskMapper);
    }

    @Test
    void listTasks_shouldReturnOkAndListOfDTOs() {
        
        Task t1 = new Task();
        Task t2 = new Task();

        TaskResponseDTO dto1 = new TaskResponseDTO();
        TaskResponseDTO dto2 = new TaskResponseDTO();

        when(taskService.findAll()).thenReturn(List.of(t1, t2));
        when(taskMapper.taskToTaskResponseDTO(t1)).thenReturn(dto1);
        when(taskMapper.taskToTaskResponseDTO(t2)).thenReturn(dto2);

        
        ResponseEntity<@NotNull List<TaskResponseDTO>> response = taskApi.listTasks();

        
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).containsExactly(dto1, dto2);

        verify(taskService).findAll();
        verify(taskMapper).taskToTaskResponseDTO(t1);
        verify(taskMapper).taskToTaskResponseDTO(t2);
        verifyNoMoreInteractions(taskService, taskMapper);
    }

    @Test
    void listTasksByStatus_shouldGroupByEachStatusAndReturnOk() {
        Task taskTodo = new Task();
        Task taskBlocked = new Task();
        Task taskDoing = new Task();
        Task taskTesting = new Task();
        Task taskDone = new Task();

        TaskResponseDTO dtoTodo = mock(TaskResponseDTO.class);
        TaskResponseDTO dtoBlocked = mock(TaskResponseDTO.class);
        TaskResponseDTO dtoDoing = mock(TaskResponseDTO.class);
        TaskResponseDTO dtoTesting = mock(TaskResponseDTO.class);
        TaskResponseDTO dtoDone = mock(TaskResponseDTO.class);

        when(dtoTodo.getStatus()).thenReturn(TaskStatus.TODO);
        when(dtoBlocked.getStatus()).thenReturn(TaskStatus.BLOCKED);
        when(dtoDoing.getStatus()).thenReturn(TaskStatus.DOING);
        when(dtoTesting.getStatus()).thenReturn(TaskStatus.TESTING);
        when(dtoDone.getStatus()).thenReturn(TaskStatus.DONE);

        when(taskService.findAll()).thenReturn(List.of(taskTodo, taskBlocked, taskDoing, taskTesting, taskDone));
        when(taskMapper.taskToTaskResponseDTO(taskTodo)).thenReturn(dtoTodo);
        when(taskMapper.taskToTaskResponseDTO(taskBlocked)).thenReturn(dtoBlocked);
        when(taskMapper.taskToTaskResponseDTO(taskDoing)).thenReturn(dtoDoing);
        when(taskMapper.taskToTaskResponseDTO(taskTesting)).thenReturn(dtoTesting);
        when(taskMapper.taskToTaskResponseDTO(taskDone)).thenReturn(dtoDone);

        
        ResponseEntity<@NotNull TaskByStatusResponseDTO> response = taskApi.listTasksByStatus();

        
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        TaskByStatusResponseDTO body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getTODO()).containsExactly(dtoTodo);
        assertThat(body.getBLOCKED()).containsExactly(dtoBlocked);
        assertThat(body.getDOING()).containsExactly(dtoDoing);
        assertThat(body.getTESTING()).containsExactly(dtoTesting);
        assertThat(body.getDONE()).containsExactly(dtoDone);

        verify(taskService).findAll();
        verify(taskMapper).taskToTaskResponseDTO(taskTodo);
        verify(taskMapper).taskToTaskResponseDTO(taskBlocked);
        verify(taskMapper).taskToTaskResponseDTO(taskDoing);
        verify(taskMapper).taskToTaskResponseDTO(taskTesting);
        verify(taskMapper).taskToTaskResponseDTO(taskDone);
        verifyNoMoreInteractions(taskService, taskMapper);
    }

    @Test
    void getTaskById_shouldReturnOkAndMappedDTO() {
        
        long id = 100L;
        Task task = new Task();
        TaskResponseDTO dto = new TaskResponseDTO();

        when(taskService.getTaskById(id)).thenReturn(task);
        when(taskMapper.taskToTaskResponseDTO(task)).thenReturn(dto);

        
        ResponseEntity<@NotNull TaskResponseDTO> response = taskApi.getTaskById(id);

        
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(dto);

        verify(taskService).getTaskById(id);
        verify(taskMapper).taskToTaskResponseDTO(task);
        verifyNoMoreInteractions(taskService, taskMapper);
    }

    @Test
    void modifyTaskStatus_shouldReturnAcceptedAndMappedDTO() {
        
        String changeJson = """
                {
                    "status": "TESTING",
                    "actor": "alice"
                }
                """;
        Task updated = new Task();
        TaskResponseDTO dto = new TaskResponseDTO();

        when(taskService.patchTask(ArgumentMatchers.any())).thenReturn(updated);
        when(taskMapper.taskToTaskResponseDTO(updated)).thenReturn(dto);

        ResponseEntity<@NotNull TaskResponseDTO> response = taskApi.patchTask(7L, changeJson);

        assertThat(response.getStatusCode().value()).isEqualTo(202);
        assertThat(response.getBody()).isEqualTo(dto);

        verify(taskService).patchTask(ArgumentMatchers.any());
        verify(taskMapper).taskToTaskResponseDTO(updated);
        verifyNoMoreInteractions(taskService, taskMapper);
    }

    @Test
    void modifyTaskUser_shouldReturnAcceptedAndMappedDTO() {
        
        String changeJson = """
                {
                    "userAffectee": "bob",
                    "actor": "alice"
                }
                """;
        Task updated = new Task();
        TaskResponseDTO dto = new TaskResponseDTO();

        when(taskService.patchTask(ArgumentMatchers.any())).thenReturn(updated);
        when(taskMapper.taskToTaskResponseDTO(updated)).thenReturn(dto);

        ResponseEntity<@NotNull TaskResponseDTO> response = taskApi.patchTask(9L, changeJson);

        assertThat(response.getStatusCode().value()).isEqualTo(202);
        assertThat(response.getBody()).isEqualTo(dto);

        verify(taskService).patchTask(ArgumentMatchers.any());
        verify(taskMapper).taskToTaskResponseDTO(updated);
        verifyNoMoreInteractions(taskService, taskMapper);
    }
}