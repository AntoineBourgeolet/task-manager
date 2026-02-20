package com.bourgeolet.task_manager.controller;

import com.bourgeolet.task_manager.dto.task.*;
import com.bourgeolet.task_manager.entity.Task;
import com.bourgeolet.task_manager.mapper.TaskMapper;
import com.bourgeolet.task_manager.service.TaskService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskApiImplTest {

    @Mock
    private TaskService taskService;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskApiImpl taskApi;

    @Test
    void createTask_shouldReturnAcceptedAndMappedDTO() {
        // Arrange
        TaskCreateDTO createDTO = mock(TaskCreateDTO.class);
        when(createDTO.getActor()).thenReturn("john.doe");

        Task mappedTask = new Task();
        Task createdTask = new Task();
        TaskResponseDTO responseDTO = new TaskResponseDTO();

        when(taskMapper.taskFromTaskCreateDTO(createDTO)).thenReturn(mappedTask);
        when(taskService.create(mappedTask, "john.doe")).thenReturn(createdTask);
        when(taskMapper.taskToTaskResponseDTO(createdTask)).thenReturn(responseDTO);

        // Act
        ResponseEntity<TaskResponseDTO> response = taskApi.createTask(createDTO);

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(202);
        assertThat(response.getBody()).isEqualTo(responseDTO);

        verify(taskMapper).taskFromTaskCreateDTO(createDTO);
        verify(taskService).create(mappedTask, "john.doe");
        verify(taskMapper).taskToTaskResponseDTO(createdTask);
        verifyNoMoreInteractions(taskService, taskMapper);
    }

    @Test
    void deleteTask_shouldReturnNoContent() {
        // Arrange
        TaskDeleteDTO deleteDTO = mock(TaskDeleteDTO.class);
        when(deleteDTO.getId()).thenReturn(42L);
        when(deleteDTO.getActor()).thenReturn("john.doe");

        doNothing().when(taskService).deleteTask(42L, "john.doe");

        // Act
        ResponseEntity<Void> response = taskApi.deleteTask(deleteDTO);

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(204);
        assertThat(response.getBody()).isNull();

        verify(taskService).deleteTask(42L, "john.doe");
        verifyNoMoreInteractions(taskService);
        verifyNoInteractions(taskMapper);
    }

    @Test
    void listTasks_shouldReturnOkAndListOfDTOs() {
        // Arrange
        Task t1 = new Task();
        Task t2 = new Task();

        TaskResponseDTO dto1 = new TaskResponseDTO();
        TaskResponseDTO dto2 = new TaskResponseDTO();

        when(taskService.findAll()).thenReturn(List.of(t1, t2));
        when(taskMapper.taskToTaskResponseDTO(t1)).thenReturn(dto1);
        when(taskMapper.taskToTaskResponseDTO(t2)).thenReturn(dto2);

        // Act
        ResponseEntity<List<TaskResponseDTO>> response = taskApi.listTasks();

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).containsExactly(dto1, dto2);

        verify(taskService).findAll();
        verify(taskMapper).taskToTaskResponseDTO(t1);
        verify(taskMapper).taskToTaskResponseDTO(t2);
        verifyNoMoreInteractions(taskService, taskMapper);
    }

    @Test
    void listTasksByStatus_shouldGroupByEachStatusAndReturnOk() {
        // Arrange
        // On crée 5 Tasks (peu importe leur contenu) et on mappe vers 5 DTOs mockés
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

        // Act
        ResponseEntity<TaskByStatusResponseDTO> response = taskApi.listTasksByStatus();

        // Assert
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
        // Arrange
        long id = 100L;
        Task task = new Task();
        TaskResponseDTO dto = new TaskResponseDTO();

        when(taskService.getTaskById(id)).thenReturn(task);
        when(taskMapper.taskToTaskResponseDTO(task)).thenReturn(dto);

        // Act
        ResponseEntity<TaskResponseDTO> response = taskApi.getTaskById(id);

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(dto);

        verify(taskService).getTaskById(id);
        verify(taskMapper).taskToTaskResponseDTO(task);
        verifyNoMoreInteractions(taskService, taskMapper);
    }

    @Test
    void modifyTaskStatus_shouldReturnAcceptedAndMappedDTO() {
        // Arrange
        TaskChangeStatusDTO changeDTO = mock(TaskChangeStatusDTO.class);
        when(changeDTO.getId()).thenReturn(7L);
        when(changeDTO.getNewStatus()).thenReturn(TaskStatus.TESTING);
        when(changeDTO.getActor()).thenReturn("alice");

        Task updated = new Task();
        TaskResponseDTO dto = new TaskResponseDTO();

        when(taskService.changeStatus(7L, TaskStatus.TESTING, "alice")).thenReturn(updated);
        when(taskMapper.taskToTaskResponseDTO(updated)).thenReturn(dto);

        // Act
        ResponseEntity<TaskResponseDTO> response = taskApi.modifyTaskStatus(changeDTO);

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(202);
        assertThat(response.getBody()).isEqualTo(dto);

        verify(taskService).changeStatus(7L, TaskStatus.TESTING, "alice");
        verify(taskMapper).taskToTaskResponseDTO(updated);
        verifyNoMoreInteractions(taskService, taskMapper);
    }

    @Test
    void modifyTaskUser_shouldReturnAcceptedAndMappedDTO() {
        // Arrange
        TaskChangeUserAffecteeDTO changeUserDTO = mock(TaskChangeUserAffecteeDTO.class);
        when(changeUserDTO.getId()).thenReturn(9L);
        when(changeUserDTO.getNewUser()).thenReturn("bob");
        when(changeUserDTO.getActor()).thenReturn("alice");

        Task updated = new Task();
        TaskResponseDTO dto = new TaskResponseDTO();

        when(taskService.changeUserAffectee(9L, "bob", "alice")).thenReturn(updated);
        when(taskMapper.taskToTaskResponseDTO(updated)).thenReturn(dto);

        // Act
        ResponseEntity<TaskResponseDTO> response = taskApi.modifyTaskUser(changeUserDTO);

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(202);
        assertThat(response.getBody()).isEqualTo(dto);

        verify(taskService).changeUserAffectee(9L, "bob", "alice");
        verify(taskMapper).taskToTaskResponseDTO(updated);
        verifyNoMoreInteractions(taskService, taskMapper);
    }
}