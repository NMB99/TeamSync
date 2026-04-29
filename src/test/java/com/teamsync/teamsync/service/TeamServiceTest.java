package com.teamsync.teamsync.service;

import com.teamsync.teamsync.dto.TeamCreateDTO;
import com.teamsync.teamsync.dto.TeamDTO;
import com.teamsync.teamsync.dto.TeamUpdateDTO;
import com.teamsync.teamsync.entity.Team;
import com.teamsync.teamsync.enums.Role;
import com.teamsync.teamsync.enums.TeamCategory;
import com.teamsync.teamsync.exception.ResourceNotFoundException;
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
                .thenAnswer(invocation -> {
                    Team t = invocation.getArgument(0);
                    t.setId(1L);
                    return t;
                });

        TeamDTO result = teamService.createTeam(newTeam);

        ArgumentCaptor<Team> captor = ArgumentCaptor.forClass(Team.class);
        verify(teamRepository).save(captor.capture());
        Team savedTeam = captor.getValue();
        assertEquals("Test Team", savedTeam.getName());
        assertEquals("This is a test team", savedTeam.getDescription());
        assertEquals(TeamCategory.TESTING, savedTeam.getCategory());

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Team",  result.getName());
        assertEquals("This is a test team", result.getDescription());
        assertEquals(TeamCategory.TESTING.name(), result.getCategory());

        SecurityContextHolder.clearContext();
    }

    @Test
    void getAllTeams_shouldThrowException_whenRoleIsAdmin() {

        Long userId = 1L;
        Long teamId = null;

        setSecurityContext(Role.ADMIN, userId, teamId);

        assertThrows(AccessDeniedException.class, () ->
                teamService.getAllTeams()
        );

        SecurityContextHolder.clearContext();
    }

    @Test
    void getAllTeams_shouldReturnAllTeams_whenRoleIsManager() {

        Long userId = 1L;
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

        Long userId = 1L;
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

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Team 1", result.get(0).getName());
        assertEquals("This is team 1", result.get(0).getDescription());
        assertEquals(TeamCategory.TESTING.name(), result.get(0).getCategory());

        SecurityContextHolder.clearContext();
    }

    @Test
    void getTeamById_shouldReturnTeamDTO_whenManagerAccessAnyTeam() {

        Long managerId = 1L;
        Long teamId1 = 1L;
        Long teamId2 = 2L;

        setSecurityContext(Role.MANAGER, managerId, teamId1);

        Team team1 = new Team();
        team1.setId(teamId1);
        team1.setName("Team 1");
        team1.setDescription("This is team 1");
        team1.setCategory(TeamCategory.TESTING);

        Team team2 = new Team();
        team2.setId(teamId2);
        team2.setName("Team 2");
        team2.setDescription("This is team 2");
        team2.setCategory(TeamCategory.TESTING);

        when(teamRepository.findById(teamId2)).thenReturn(Optional.of(team2));

        TeamDTO result = teamService.getTeamById(teamId2);

        verify(teamRepository).findById(teamId2);

        assertNotNull(result);
        assertEquals("Team 2", result.getName());
        assertEquals("This is team 2", result.getDescription());
        assertEquals(TeamCategory.TESTING.name(), result.getCategory());

        SecurityContextHolder.clearContext();
    }

    @Test
    void getTeamById_shouldReturnTeamDTO_whenTeamLeadAccessesOwnTeam() {

        Long teamLeadId = 1L;
        Long teamId1 = 1L;

        setSecurityContext(Role.TEAM_LEAD, teamLeadId, teamId1);

        Team team1 = new Team();
        team1.setId(teamId1);
        team1.setName("Team 1");
        team1.setDescription("This is team 1");
        team1.setCategory(TeamCategory.TESTING);

        when(teamRepository.findById(teamId1)).thenReturn(Optional.of(team1));

        TeamDTO result = teamService.getTeamById(teamId1);

        verify(teamRepository).findById(teamId1);

        assertNotNull(result);
        assertEquals("Team 1", result.getName());
        assertEquals("This is team 1", result.getDescription());
        assertEquals(TeamCategory.TESTING.name(), result.getCategory());

        SecurityContextHolder.clearContext();
    }

    @Test
    void getTeamById_shouldThrowException_whenTeamLeadAccessesOtherTeam() {

        Long teamLeadId = 1L;
        Long teamId1 = 1L;
        Long teamId2 = 2L;

        setSecurityContext(Role.TEAM_LEAD, teamLeadId, teamId1);

        assertThrows(AccessDeniedException.class, () ->
                teamService.getTeamById(teamId2)
        );

        verifyNoInteractions(teamRepository);

        SecurityContextHolder.clearContext();
    }

    @Test
    void getTeamById_shouldReturnTeamDTO_whenTeamMemberAccessesOwnTeam() {

        Long teamLeadId = 1L;
        Long teamId1 = 1L;

        setSecurityContext(Role.TEAM_MEMBER, teamLeadId, teamId1);

        Team team1 = new Team();
        team1.setId(teamId1);
        team1.setName("Team 1");
        team1.setDescription("This is team 1");
        team1.setCategory(TeamCategory.TESTING);

        when(teamRepository.findById(teamId1)).thenReturn(Optional.of(team1));

        TeamDTO result = teamService.getTeamById(teamId1);

        verify(teamRepository).findById(teamId1);

        assertNotNull(result);
        assertEquals("Team 1", result.getName());
        assertEquals("This is team 1", result.getDescription());
        assertEquals(TeamCategory.TESTING.name(), result.getCategory());

        SecurityContextHolder.clearContext();
    }

    @Test
    void getTeamById_shouldThrowException_whenTeamMemberAccessesOtherTeam() {

        Long teamLeadId = 1L;
        Long teamId1 = 1L;
        Long teamId2 = 2L;

        setSecurityContext(Role.TEAM_MEMBER, teamLeadId, teamId1);

        assertThrows(AccessDeniedException.class, () ->
                teamService.getTeamById(teamId2)
        );

        verifyNoInteractions(teamRepository);

        SecurityContextHolder.clearContext();
    }

    @Test
    void getTeamById_shouldThrowException_whenTeamNotFound() {

        Long teamLeadId = 1L;
        Long teamId = 1L;

        setSecurityContext(Role.MANAGER, teamLeadId, null);

        when(teamRepository.findById(teamId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                teamService.getTeamById(teamId)
        );

        verify(teamRepository).findById(teamId);

        SecurityContextHolder.clearContext();
    }

    @Test
    void updateTeam_shouldReturnTeamDTO_whenTeamExists() {

        Long teamId = 1L;
        Long managerId = 1L;

        setSecurityContext(Role.MANAGER, managerId, teamId);

        TeamUpdateDTO updateDto = new TeamUpdateDTO();
        updateDto.setName("Updated Team 1");
        updateDto.setDescription("This is updated team 1");
        updateDto.setCategory(TeamCategory.DEVELOPMENT);

        Team team = new Team();
        team.setId(teamId);
        team.setName("Team 1");
        team.setDescription("This is team 1");
        team.setCategory(TeamCategory.TESTING);

        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));
        when(teamRepository.save(any(Team.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TeamDTO result = teamService.updateTeam(teamId, updateDto);
        
        verify(teamRepository).findById(teamId);

        ArgumentCaptor<Team> captor = ArgumentCaptor.forClass(Team.class);
        verify(teamRepository).save(captor.capture());
        Team updatedTeam = captor.getValue();
        assertEquals(teamId, updatedTeam.getId());
        assertEquals(updateDto.getName(), updatedTeam.getName());
        assertEquals(updateDto.getDescription(), updatedTeam.getDescription());
        assertEquals(updateDto.getCategory(), updatedTeam.getCategory());

        assertNotNull(result);
        assertEquals(updateDto.getName(), result.getName());
        assertEquals(updateDto.getDescription(), result.getDescription());
        assertEquals(updateDto.getCategory().name(), result.getCategory());

        SecurityContextHolder.clearContext();
    }

    @Test
    void updateTeam_shouldThrowException_whenTeamNotFound() {

        Long teamId = 1L;
        Long managerId = 1L;

        setSecurityContext(Role.MANAGER, managerId, null);

        when(teamRepository.findById(teamId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                teamService.updateTeam(teamId, new TeamUpdateDTO())
        );

        verify(teamRepository).findById(teamId);
        verify(teamRepository, never()).save(any());

        SecurityContextHolder.clearContext();
    }

    @Test
    void deleteTeam_shouldDeleteTeam_whenTeamExists() {

        Long teamId = 1L;
        Long managerId = 1L;

        setSecurityContext(Role.MANAGER, managerId, teamId);

        Team team = new Team();
        team.setId(teamId);
        team.setName("Team 1");
        team.setDescription("This is team 1");
        team.setCategory(TeamCategory.TESTING);

        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));

        teamService.deleteTeam(teamId);

        verify(teamRepository).findById(teamId);
        verify(teamRepository).deleteById(teamId);
        verify(teamRepository, never()).save(any());

        SecurityContextHolder.clearContext();
    }

    @Test
    void deleteTeam_shouldThrowException_whenTeamNotFound() {

        Long teamId = 1L;
        Long managerId = 1L;

        setSecurityContext(Role.MANAGER, managerId, null);

        when(teamRepository.findById(teamId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                teamService.deleteTeam(teamId)
        );

        verify(teamRepository).findById(teamId);
        verify(teamRepository, never()).save(any());

        SecurityContextHolder.clearContext();
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