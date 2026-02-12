package com.bourgeolet.task_manager.mapper;

import com.bourgeolet.task_manager.dto.user.UserCreateDTO;
import com.bourgeolet.task_manager.dto.user.UserResponseDTO;
import com.bourgeolet.task_manager.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class UserMapper {


    public UserResponseDTO userToUserResponseDTO(User user) {
        return new UserResponseDTO(user.getUsername(), user.getEmail());
    }

    public User userCreateDTOToUser(@Valid UserCreateDTO dto) {
        User user = new User();
        user.setUsername(dto.username());
        user.setEmail(dto.email());
        return user;
    }
}
