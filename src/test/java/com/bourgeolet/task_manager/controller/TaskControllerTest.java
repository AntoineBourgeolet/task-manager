package com.bourgeolet.task_manager.controller;

import com.bourgeolet.task_manager.dto.TaskCreateDTO;
import com.bourgeolet.task_manager.dto.TaskResponseDTO;
import com.bourgeolet.task_manager.entity.Task;
import com.bourgeolet.task_manager.entity.User;
import com.bourgeolet.task_manager.exception.UserNotFoundException;
import com.bourgeolet.task_manager.service.TaskService;
import com.bourgeolet.task_manager.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

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
        when(userService.getUserById(1L)).thenReturn(new User());
        when(taskService.create(any())).thenReturn(new TaskResponseDTO(10L, "Titre", "Desc", "User",false));

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TaskCreateDTO(1L, "Titre", "Desc"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10));
    }

    @Test
    void create_ok_with_no_user() throws Exception {
        when(taskService.create(any())).thenReturn(new TaskResponseDTO(10L, "Titre", "Desc", null,false));

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TaskCreateDTO(null, "Titre", "Desc"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userAffectee").isEmpty());

        verify(userService, never()).getUserById(any());
    }

    @Test
    void create_ok_user_not_found() throws Exception {
        Long id = 999L;
        when(userService.getUserById(id)).thenThrow(new UserNotFoundException(id));

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TaskCreateDTO(id, "Titre", "Desc"))))
                .andExpect(status().isNotFound());


    }

    @Test
    void create_ko_no_title() throws Exception {

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TaskCreateDTO(1L, null, "Desc"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void all() throws Exception {


        when(taskService.create(any())).thenReturn(new TaskResponseDTO(10L, "Titre", "Desc", "User", false));


        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk());
    }
}

