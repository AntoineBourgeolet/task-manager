package com.bourgeolet.task_manager.service;

import com.bourgeolet.task_manager.dto.user.UserCreateDTO;
import com.bourgeolet.task_manager.dto.user.UserResponseDTO;
import com.bourgeolet.task_manager.entity.User;
import com.bourgeolet.task_manager.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        super();
        this.userRepository = userRepository;
    }

    public UserResponseDTO create(User user) {
        User newUser = new User();
        newUser.setUsername(user.username());
        newUser.setEmail(user.email());
        return toUserResponseDTO(userRepository.save(newUser);
    }

    public List<UserResponseDTO> findAll() {
        List<User> userList = userRepository.findAll();
        return userList.stream()
                .map(this::toUserResponseDTO)
                .toList();

    }

    public User getUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    private UserResponseDTO toUserResponseDTO(User user) {
        return new UserResponseDTO(
                user.getUsername(),
                user.getEmail()
        );
    }

    public Boolean exitByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
}
