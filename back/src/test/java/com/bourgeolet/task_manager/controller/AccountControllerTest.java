package com.bourgeolet.task_manager.controller;

import com.bourgeolet.task_manager.dto.task.TaskResponseDTO;
import com.bourgeolet.task_manager.dto.account.AccountCreateDTO;
import com.bourgeolet.task_manager.dto.account.AccountResponseDTO;
import com.bourgeolet.task_manager.entity.Task;
import com.bourgeolet.task_manager.model.task.TaskStatus;
import com.bourgeolet.task_manager.service.TaskService;
import com.bourgeolet.task_manager.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
class AccountControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    TaskService taskService;

    @MockitoBean
    AccountService accountService;

    @Autowired
    ObjectMapper objectMapper;

    List<TaskResponseDTO> listTaskResponse = new ArrayList<>();
    AccountCreateDTO userCreateKo = new AccountCreateDTO("actor",null, "email@email.test");
    AccountCreateDTO userCreate = new AccountCreateDTO("actor","Username", "email@email.test");

    @BeforeEach
    void setUp() {
        listTaskResponse.add(new TaskResponseDTO(10L, "Titre1", "Desc1", "User1", 1,null, TaskStatus.TODO));
        listTaskResponse.add(new TaskResponseDTO(20L, "Titre2", "Desc2", "User2", 1,null, TaskStatus.TODO));
        listTaskResponse.add(new TaskResponseDTO(30L, "Titre3", "Desc3", "User3", 1,null, TaskStatus.TODO));
    }

    @Test
    void create_ok() throws Exception {
        when(accountService.create(any())).thenReturn(new Task().getAccount());
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Username"));
    }

    @Test
    void create_ko_no_username() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreateKo)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void all() throws Exception {
        when(accountService.create(any())).thenReturn(new Task().getAccount());

        mockMvc.perform(get("/users"))
                        .andExpect(status().isOk());

    }

    @Test
    void findByUserId() throws Exception {
        when(taskService.getTasksByUserId("Username")).thenReturn(new ArrayList<>());
        mockMvc.perform(get("/users/10/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Titre1"));
    }


}