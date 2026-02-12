package com.bourgeolet.task_manager.service;

import com.bourgeolet.task_manager.dto.user.UserResponseDTO;
import com.bourgeolet.task_manager.entity.User;
import com.bourgeolet.task_manager.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {


    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserService userService;


    private User userToSave;
    private List<User> listUsers;
    private User userSimple1;


    @BeforeEach
    void setUp() {
        userToSave = new User();
        userToSave.setUsername("Username");
        userToSave.setEmail("email@email.com");

        userSimple1 = new User();
        userSimple1.setUsername("username1");
        userSimple1.setEmail("email1@email.com");

        User userSimple2 = new User();
        userSimple2.setUsername("username2");
        userSimple2.setEmail("email2@email.com");

        listUsers = new ArrayList<>();
        listUsers.add(userSimple1);
        listUsers.add(userSimple2);
    }

    @Test
    void create() {
        when(userRepository.save(any(User.class))).thenReturn(userToSave);

        UserResponseDTO response = userService.create(userToSave);

        assertNotNull(response);
        assertEquals("username", response.username());
        assertEquals("email@email.com", response.email());

        verify(userRepository).save(userToSave);
    }

    @Test
    void create_with_no_username() {

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> userService.create(null)
        );
        assertEquals("username is required", ex.getMessage());

    }

    @Test
    void findAll() {
        when(userRepository.findAll()).thenReturn(listUsers);

        List<UserResponseDTO> userResponseDTOList = userService.findAll();

        assertNotNull(userResponseDTOList);
        assertEquals("username1", userResponseDTOList.get(0).username());
        assertEquals("username2", userResponseDTOList.get(1).username());
        assertEquals("email1@email.com", userResponseDTOList.get(0).email());
        assertEquals("email2@email.com", userResponseDTOList.get(1).email());

        verify(userRepository).findAll();
    }


}