package com.bourgeolet.task_manager.service;

import com.bourgeolet.task_manager.dto.user.UserResponseDTO;
import com.bourgeolet.task_manager.entity.User;
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


    public UserResponseDTO create(User user) {
        return userMapper.userToUserResponseDTO(userRepository.save(user));
    }

    public List<UserResponseDTO> findAll() {
        List<User> userList = userRepository.findAll();
        return userList.stream().map(userMapper::userToUserResponseDTO).toList();

    }

    public User getUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

}
