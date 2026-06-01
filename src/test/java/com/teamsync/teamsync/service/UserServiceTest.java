package com.teamsync.teamsync.service;

import com.teamsync.teamsync.dto.UserCreateDTO;
import com.teamsync.teamsync.dto.UserCreatedDTO;
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
import static org.mockito.Mockito.*;

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
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserCreatedDTO result = userService.createUser(newUser);

        verify(userRepository).existsByEmail(newUser.getEmail());
        verify(passwordEncoder).encode(anyString());
        verify(teamService).getTeamEntityById(newUser.getTeamId());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User user = captor.getValue();
        assertEquals(newUser.getFullName(), user.getFullName());
        assertEquals(newUser.getEmail(), user.getEmail());
        assertEquals(newUser.getRole(), user.getRole());

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

        verify(userRepository).existsByEmail(newUser.getEmail());
        verify(userRepository, never()).save(any());
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

        verify(userRepository).findAll();

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("Alex Mark", result.get(0).getFullName());
        assertEquals("David Rolo", result.get(1).getFullName());
        assertEquals("Sophie Best", result.get(2).getFullName());

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

        verify(userRepository).findAll();

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("Alex Mark", result.get(0).getFullName());
        assertEquals("David Rolo", result.get(1).getFullName());
        assertEquals("Sophie Best", result.get(2).getFullName());

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

        verify(userRepository).findByTeamId(teamId);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("Alex Mark", result.get(0).getFullName());
        assertEquals("David Rolo", result.get(1).getFullName());
        assertEquals("Sophie Best", result.get(2).getFullName());

        SecurityContextHolder.clearContext();
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

        verify(userRepository).findById(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("Alex Mark", result.getFullName());
        assertEquals("alex.mark@test.com",  result.getEmail());
    }

    @Test
    void getUserById_shouldThrowException_whenUserDoesNotExists() {

        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUserById(1L);
        });

        verify(userRepository).findById(userId);
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
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserDTO result = userService.updateUser(userId, updateDTO);

        verify(userRepository).findById(userId);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User updatedUser = captor.getValue();
        assertEquals(updateDTO.getFullName(), updatedUser.getFullName());

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals(updateDTO.getFullName(), result.getFullName());
    }

    @Test
    void updateUser_shouldThrowException_whenUserDoesNotExists() {

        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            userService.updateUser(userId, new UserUpdateDTO());
        });

        verify(userRepository).findById(userId);
        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteUser_shouldDeleteUser_whenUserExists() {

        Long userId = 1L;

        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.deleteUser(userId);

        verify(userRepository).findById(userId);
        verify(userRepository).delete(user);
        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteUser_shouldThrowException_whenUserDoesNotExists() {

        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            userService.deleteUser(userId);
        });

        verify(userRepository).findById(userId);
        verify(userRepository, never()).delete(any());
        verify(userRepository, never()).save(any());
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