package com.bourgeolet.task_manager.controller;

import com.bourgeolet.task_manager.dto.task.TaskCreateDTO;
import com.bourgeolet.task_manager.dto.task.TaskResponseDTO;
import com.bourgeolet.task_manager.entity.Users;
import com.bourgeolet.task_manager.exception.user.UserNotFoundException;
import com.bourgeolet.task_manager.model.task.TaskStatus;
import com.bourgeolet.task_manager.service.TaskService;
import com.bourgeolet.task_manager.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
class TasksControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    TaskService taskService;

    @MockitoBean
    UserService userService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void create_ok() throws Exception {
        when(userService.getUserByUsername("Username")).thenReturn(new Users());
        when(taskService.create(any(),"Actor")).thenReturn(new TaskResponseDTO(10L, "Titre", "Desc", "User", 1,null, TaskStatus.TODO));

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TaskCreateDTO("actor","username","title","desc", 1, null))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10));
    }

    @Test
    void create_ok_with_no_user() throws Exception {
        when(taskService.create(any(),"Actor")).thenReturn(new TaskResponseDTO(10L, "Titre", "Desc", null, 1,null, TaskStatus.TODO));

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TaskCreateDTO("actor",null,"title","des", 1, null))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userAffectee").isEmpty());

        verify(userService, never()).getUserByUsername(any());
    }

    @Test
    void create_ok_user_not_found() throws Exception {
        String id = "Username";
        when(userService.getUserByUsername(id)).thenThrow(new UserNotFoundException(id));

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TaskCreateDTO("actor",null,"title",null, 1, null))))
                .andExpect(status().isNotFound());


    }

    @Test
    void create_ko_no_title() throws Exception {

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TaskCreateDTO("actor",null,null,null, 1, null))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void all() throws Exception {


        when(taskService.create(any(),"Actor")).thenReturn(new TaskResponseDTO(10L, "Titre", "Desc", "User",1, null , TaskStatus.TODO));


        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk());
    }
}

