package com.teamsync.teamsync.service;

import com.teamsync.teamsync.dto.TeamCreateDTO;
import com.teamsync.teamsync.dto.TeamDTO;
import com.teamsync.teamsync.entity.Team;
import com.teamsync.teamsync.enums.Role;
import com.teamsync.teamsync.enums.TeamCategory;
import com.teamsync.teamsync.repository.TeamRepository;
import com.teamsync.teamsync.security.CustomUserDetails;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

    @Mock
    private TeamRepository teamRepository;

    @InjectMocks
    private TeamService teamService;

    @Test
    void createTeam_shouldReturnTeamDto_whenTeamIsCreated() {

        TeamCreateDTO newTeam = new TeamCreateDTO();
        newTeam.setName("Test Team");
        newTeam.setDescription("This is a test team");
        newTeam.setCategory(TeamCategory.TESTING);

        when(teamRepository.save(any(Team.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TeamDTO result = teamService.createTeam(newTeam);

        ArgumentCaptor<Team> captor = ArgumentCaptor.forClass(Team.class);
        verify(teamRepository).save(captor.capture());
        Team savedTeam = captor.getValue();

        assertNotNull(result);
        assertEquals("Test Team", savedTeam.getName());
        assertEquals("This is a test team", savedTeam.getDescription());
        assertEquals(TeamCategory.TESTING, savedTeam.getCategory());
    }

    @Test
    void getAllTeams_shouldThrowException_whenRoleIsAdmin() {

        long userId = 1L;
        Long teamId = null;

        setSecurityContext(Role.ADMIN, userId, teamId);

        assertThrows(AccessDeniedException.class, () ->
                teamService.getAllTeams()
        );
    }

    @Test
    void getAllTeams_shouldReturnAllTeams_whenRoleIsManager() {

        long userId = 1L;
        Long teamId = null;

        setSecurityContext(Role.MANAGER, userId, teamId);

        Team team1 = new Team();
        team1.setId(1L);
        team1.setName("Team 1");
        team1.setDescription("This is team 1");
        team1.setCategory(TeamCategory.TESTING);

        Team team2 = new Team();
        team2.setId(2L);
        team2.setName("Team 2");
        team2.setDescription("This is team 2");
        team2.setCategory(TeamCategory.DEVELOPMENT);

        when(teamRepository.findAll()).thenReturn(List.of(team1, team2));

        List<TeamDTO> result = teamService.getAllTeams();

        verify(teamRepository).findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Team 1", result.get(0).getName());
        assertEquals("This is team 1", result.get(0).getDescription());
        assertEquals(TeamCategory.TESTING.name(), result.get(0).getCategory());

        assertEquals("Team 2", result.get(1).getName());
        assertEquals("This is team 2", result.get(1).getDescription());
        assertEquals(TeamCategory.DEVELOPMENT.name(), result.get(1).getCategory());

        SecurityContextHolder.clearContext();
    }

    @Test
    void getAllTeams_shouldReturnOwnTeams_whenRoleIsTeamLead() {

        long userId = 1L;
        Long teamId = 1L;

        setSecurityContext(Role.TEAM_LEAD, userId, teamId);

        Team team1 = new Team();
        team1.setId(teamId);
        team1.setName("Team 1");
        team1.setDescription("This is team 1");
        team1.setCategory(TeamCategory.TESTING);

        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team1));

        List<TeamDTO> result = teamService.getAllTeams();

        verify(teamRepository).findById(teamId);
        verify(teamRepository, never()).findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Team 1", result.get(0).getName());
        assertEquals("This is team 1", result.get(0).getDescription());
        assertEquals(TeamCategory.TESTING.name(), result.get(0).getCategory());

        SecurityContextHolder.clearContext();
    }

    @Test
    void getTeamEntityById() {
    }

    @Test
    void getTeamById() {
    }

    @Test
    void updateTeam() {
    }

    @Test
    void deleteTeam() {
    }

    private void setSecurityContext(Role role, Long userId, Long teamId) {

        CustomUserDetails mockuser = new CustomUserDetails(
                userId, "test@test.com", "password", role.name(), teamId,
                List.of(new SimpleGrantedAuthority("ROLE_" + role.name()))
        );

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                mockuser, null, mockuser.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}