package com.bourgeolet.task_manager.mapper;

import com.bourgeolet.task_manager.dto.user.UserCreateDTO;
import com.bourgeolet.task_manager.dto.user.UserResponseDTO;
import com.bourgeolet.task_manager.entity.Users;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class UserMapper {


    public UserResponseDTO userToUserResponseDTO(Users users) {
        return new UserResponseDTO(users.getUsername(), users.getEmail());
    }

    public Users userCreateDTOToUser(@Valid UserCreateDTO dto) {
        Users users = new Users();
        users.setUsername(dto.username());
        users.setEmail(dto.email());
        return users;
    }
}
