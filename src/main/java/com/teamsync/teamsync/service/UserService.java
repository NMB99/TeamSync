package com.teamsync.teamsync.service;

import com.teamsync.teamsync.dto.UserCreateDTO;
import com.teamsync.teamsync.dto.UserDTO;
import com.teamsync.teamsync.dto.UserUpdateDTO;
import com.teamsync.teamsync.entity.User;
import com.teamsync.teamsync.exception.BadRequestException;
import com.teamsync.teamsync.exception.ResourceNotFoundException;
import com.teamsync.teamsync.repository.UserRepository;
import com.teamsync.teamsync.util.PasswordUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final TeamService teamService;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, TeamService teamService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.teamService = teamService;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDTO createUser(UserCreateDTO userDTO) {
        if (userRepository.existsByEmail((userDTO.getEmail()))) {
            throw new BadRequestException("Email already exists");
        }

        User user = new User();
        user.setFullName(userDTO.getFullName());
        user.setEmail(userDTO.getEmail());
        user.setRole(userDTO.getRole());

        String password = PasswordUtil.generateRandomPassword();
        String encodedPassword = passwordEncoder.encode(password);

        System.out.println("Password: " + password);
        user.setPassword(encodedPassword);

        if (userDTO.getTeamId() != null) {
            user.setTeam(teamService.getTeamEntityById(userDTO.getTeamId()));
        }

        User savedUser = userRepository.save(user);
        return convertUserToDTO(savedUser);
    }

    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::convertUserToDTO)
                .toList();
    }

    public User getUserEntityById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with \"id: " + id + "\" not found"));
    }

    public UserDTO getUserById(Long id) {
        User user = getUserEntityById(id);
        return convertUserToDTO(user);
    }

    public UserDTO updateUser(Long id, UserUpdateDTO user) {
        User updateUser = getUserEntityById(id);
        if (user.getFullName() != null) {
            updateUser.setFullName(user.getFullName());
        }
        if (user.getEmail() != null) {
            updateUser.setEmail(user.getEmail());
        }
        if (user.getTeamId() != null) {
            updateUser.setTeam(teamService.getTeamEntityById(user.getTeamId()));
        }
        return convertUserToDTO(userRepository.save(updateUser));
    }

    public void deleteUser(Long id) {
        getUserEntityById(id);
        userRepository.deleteById(id);
    }

    private UserDTO convertUserToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setFullName(user.getFullName());
        userDTO.setEmail(user.getEmail());
        userDTO.setRole(user.getRole().toString());
        return userDTO;
    }
}
