package com.bourgeolet.task_manager.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class AuthorizationMatrixIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "standard-user", roles = {"USER"})
    void userCannotAccessAccountEndpoints() throws Exception {
        mockMvc.perform(get("/account"))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "observer-user", roles = {"OBSERVER"})
    void observerCannotAccessAccountEndpoints() throws Exception {
        mockMvc.perform(get("/account"))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "observer-user", roles = {"OBSERVER"})
    void observerCannotCreateTask() throws Exception {
        mockMvc.perform(post("/task")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "actor": "observer-user",
                      "title": "forbidden-task"
                    }
                    """))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "observer-user", roles = {"OBSERVER"})
    void observerCannotDeleteTag() throws Exception {
        mockMvc.perform(delete("/tag")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "actor": "observer-user",
                      "id": 1
                    }
                    """))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void adminCanAccessAccountEndpoints() throws Exception {
        mockMvc.perform(get("/account"))
            .andExpect(status().isAccepted());
    }
}

