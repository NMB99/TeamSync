package com.teamsync.teamsync.service;

import com.teamsync.teamsync.dto.UserUpdateDTO;
import com.teamsync.teamsync.entity.User;
import com.teamsync.teamsync.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUser(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User updateUser(Long id, @Valid UserUpdateDTO user) {
        User updateUser = userRepository.findById(id).orElse(null);
        if (updateUser != null) {
            if (user.getFullName() != null) {
                updateUser.setFullName(user.getFullName());
            }
            if (user.getEmail() != null) {
                updateUser.setEmail(user.getEmail());
            }
            return userRepository.save(updateUser);
        }
        return null;
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
