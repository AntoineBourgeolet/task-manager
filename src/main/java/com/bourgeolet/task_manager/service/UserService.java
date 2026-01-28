package com.bourgeolet.task_manager.service;

import com.bourgeolet.task_manager.dto.UserResponseDTO;
import com.bourgeolet.task_manager.entity.User;
import com.bourgeolet.task_manager.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
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

        if (user == null || user.getUsername() == null || user.getUsername().isBlank()) {
            throw new IllegalArgumentException("username is required");
        }
        user.setUsername(user.getUsername().toLowerCase());
        User userResponse = userRepository.save(user);
        return toUserResponseDTO(userResponse);
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
}
