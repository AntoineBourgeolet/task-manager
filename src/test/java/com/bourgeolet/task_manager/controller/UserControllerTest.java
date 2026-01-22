package com.bourgeolet.task_manager.controller;

import com.bourgeolet.task_manager.dto.UserResponseDTO;
import com.bourgeolet.task_manager.entity.User;
import com.bourgeolet.task_manager.repository.UserRepository;
import com.bourgeolet.task_manager.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserService userService;


    @Test
    void createUser_success() {
        User input = new User();
        input.setUsername("Test");

        User saved = new User();
        saved.setId(1L);
        saved.setUsername("test");
        when(userRepository.save(any(User.class))).thenReturn(saved);

        UserResponseDTO result = userService.create(input);

        assertEquals("test", result.username());
        assertEquals(1L, result.id());
    }

    @Test
    void createUser_missingUsername_shouldFail() {
        User input = new User();
        input.setEmail("test@example.com");

        assertThrows(IllegalArgumentException.class, () -> userService.create(input));
    }

    @Test
    void create() {
    }

    @Test
    void all() {
    }
}