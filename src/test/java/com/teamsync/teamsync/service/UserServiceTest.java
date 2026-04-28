package com.teamsync.teamsync.service;

import com.teamsync.teamsync.dto.UserCreateDTO;
import com.teamsync.teamsync.dto.UserDTO;
import com.teamsync.teamsync.dto.UserUpdateDTO;
import com.teamsync.teamsync.entity.Team;
import com.teamsync.teamsync.entity.User;
import com.teamsync.teamsync.enums.Role;
import com.teamsync.teamsync.exception.BadRequestException;
import com.teamsync.teamsync.exception.ResourceNotFoundException;
import com.teamsync.teamsync.repository.UserRepository;
import com.teamsync.teamsync.security.CustomUserDetails;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TeamService teamService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void createUser_shouldReturnUserDto_whenEmailDoesNotExists() {

        UserCreateDTO newUser = new UserCreateDTO();
        newUser.setFullName("Alex Mark");
        newUser.setEmail("alex.mark@test.com");
        newUser.setRole(Role.TEAM_MEMBER);
        newUser.setTeamId(1L);

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setFullName(newUser.getFullName());
        savedUser.setEmail(newUser.getEmail());
        savedUser.setRole(newUser.getRole());

        Team team = new Team();
        team.setId(newUser.getTeamId());
        team.setName("Test Team");

        savedUser.setTeam(team);

        when(userRepository.existsByEmail(newUser.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(teamService.getTeamEntityById(newUser.getTeamId())).thenReturn(team);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserDTO result = userService.createUser(newUser);

        assertNotNull(result);
        assertEquals("Alex Mark", result.getFullName());
        assertEquals("alex.mark@test.com", result.getEmail());
    }

    @Test
    void createUser_shouldThrowException_whenEmailAlreadyExists() {

        UserCreateDTO newUser = new UserCreateDTO();
        newUser.setFullName("Alex Mark");
        newUser.setEmail("alex.mark@test.com");
        newUser.setRole(Role.TEAM_MEMBER);
        newUser.setTeamId(1L);

        when(userRepository.existsByEmail(newUser.getEmail())).thenReturn(true);

        assertThrows(BadRequestException.class, () -> {
            userService.createUser(newUser);
        });
    }

    @Test
    void getAllUsers_shouldReturnAllUsers_whenUserIsAdmin() {

        setSecurityContext(Role.ADMIN, null);

        User user1 = new User();
        user1.setId(1L);
        user1.setFullName("Alex Mark");
        user1.setEmail("alex.mark@test.com");
        user1.setRole(Role.TEAM_MEMBER);

        User user2 = new User();
        user2.setId(2L);
        user2.setFullName("David Rolo");
        user2.setEmail("david.rolo@test.com");
        user2.setRole(Role.TEAM_LEAD);

        User user3 = new User();
        user3.setId(3L);
        user3.setFullName("Sophie Best");
        user3.setEmail("sophie.best@test.com");
        user3.setRole(Role.MANAGER);

        when(userRepository.findAll()).thenReturn(List.of(user1, user2, user3));

        List<UserDTO> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(3, result.size());

        SecurityContextHolder.clearContext();
    }

    @Test
    void getAllUsers_shouldReturnAllUsers_whenUserIsManager() {

        setSecurityContext(Role.MANAGER, null);

        User user1 = new User();
        user1.setId(1L);
        user1.setFullName("Alex Mark");
        user1.setEmail("alex.mark@test.com");
        user1.setRole(Role.TEAM_MEMBER);

        User user2 = new User();
        user2.setId(2L);
        user2.setFullName("David Rolo");
        user2.setEmail("david.rolo@test.com");
        user2.setRole(Role.TEAM_LEAD);

        User user3 = new User();
        user3.setId(3L);
        user3.setFullName("Sophie Best");
        user3.setEmail("sophie.best@test.com");
        user3.setRole(Role.MANAGER);

        when(userRepository.findAll()).thenReturn(List.of(user1, user2, user3));

        List<UserDTO> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(3, result.size());

        SecurityContextHolder.clearContext();
    }

    @Test
    void getAllUsers_shouldReturnAllUsers_whenUserIsTeamLead() {

        Long teamId = 1L;

        setSecurityContext(Role.TEAM_LEAD, teamId);

        Team team = new Team();
        team.setId(teamId);
        team.setName("Test Team");

        User user1 = new User();
        user1.setId(1L);
        user1.setFullName("Alex Mark");
        user1.setEmail("alex.mark@test.com");
        user1.setRole(Role.TEAM_MEMBER);
        user1.setTeam(team);

        User user2 = new User();
        user2.setId(2L);
        user2.setFullName("David Rolo");
        user2.setEmail("david.rolo@test.com");
        user2.setRole(Role.TEAM_LEAD);
        user2.setTeam(team);

        User user3 = new User();
        user3.setId(3L);
        user3.setFullName("Sophie Best");
        user3.setEmail("sophie.best@test.com");
        user3.setRole(Role.MANAGER);
        user3.setTeam(team);

        when(userRepository.findByTeamId(teamId)).thenReturn(List.of(user1, user2, user3));

        List<UserDTO> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(3, result.size());

        SecurityContextHolder.clearContext();
    }

    /*
        Same as getUserById. Also, it is indirectly tested.
     */
    @Test
    void getUserEntityById() {
    }

    @Test
    void getUserById_shouldReturnUserDto_whenUserExists() {

        Long userId = 1L;

        User user = new User();
        user.setId(userId);
        user.setFullName("Alex Mark");
        user.setEmail("alex.mark@test.com");
        user.setRole(Role.TEAM_MEMBER);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserDTO result = userService.getUserById(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("Alex Mark", result.getFullName());
    }

    @Test
    void getUserById_shouldThrowException_whenUserDoesNotExists() {

        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUserById(1L);
        });
    }

    @Test
    void updateUser_shouldReturnUpdatedUserDto_whenUserExists() {

        Long userId = 1L;

        UserUpdateDTO updateDTO = new UserUpdateDTO();
        updateDTO.setFullName("Alexa Mark");

        User savedUser = new User();
        savedUser.setId(userId);
        savedUser.setFullName("Alex Mark");
        savedUser.setRole(Role.TEAM_MEMBER);

        when(userRepository.findById(userId)).thenReturn(Optional.of(savedUser));
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserDTO result = userService.updateUser(userId, updateDTO);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User updatedUser = captor.getValue();
        assertEquals("Alexa Mark", updatedUser.getFullName());

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("Alexa Mark", result.getFullName());
    }

    @Test
    void updateUser_shouldThrowException_whenUserDoesNotExists() {

        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            userService.updateUser(userId, new UserUpdateDTO());
        });
    }

    @Test
    void deleteUser_shouldDeleteUser_whenUserExists() {

        Long userId = 1L;

        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.deleteUser(userId);

        verify(userRepository).delete(user);
    }

    @Test
    void deleteUser_shouldThrowException_whenUserDoesNotExists() {

        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            userService.deleteUser(userId);
        });
    }

    private void setSecurityContext(Role role, Long teamId) {

        CustomUserDetails mockUser = new CustomUserDetails(
                1L, "test@test.com", "password", role.name(), teamId,
                List.of(new SimpleGrantedAuthority("ROLE_" + role.name()))
        );

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                mockUser, null, mockUser.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}