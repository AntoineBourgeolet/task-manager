package com.bourgeolet.task_manager.service;

import com.bourgeolet.task_manager.dto.user.UserResponseDTO;
import com.bourgeolet.task_manager.entity.Users;
import com.bourgeolet.task_manager.mapper.UserMapper;
import com.bourgeolet.task_manager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;


    public UserResponseDTO create(Users users) {
        return userMapper.userToUserResponseDTO(userRepository.save(users));
    }

    public List<UserResponseDTO> findAll() {
        List<Users> usersList = userRepository.findAll();
        return usersList.stream().map(userMapper::userToUserResponseDTO).toList();

    }

    public Users getUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

}
