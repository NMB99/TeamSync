package com.teamsync.teamsync.service;

import com.teamsync.teamsync.dto.StandupCreateDTO;
import com.teamsync.teamsync.dto.StandupDTO;
import com.teamsync.teamsync.dto.StandupUpdateDTO;
import com.teamsync.teamsync.entity.Standup;
import com.teamsync.teamsync.entity.Team;
import com.teamsync.teamsync.entity.User;
import com.teamsync.teamsync.enums.Role;
import com.teamsync.teamsync.exception.ResourceNotFoundException;
import com.teamsync.teamsync.repository.StandupRepository;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StandupServiceTest {

    @Mock
    private StandupRepository standupRepository;

    @Mock
    private UserService userService;

    @Mock
    private TeamService teamService;

    @InjectMocks
    private StandupService standupService;

    @Test
    void createStandup_shouldReturnStandupDto_whenUserExists() {

        Long userId = 1L;
        Long teamId = 1L;

        setSecurityContext(Role.TEAM_MEMBER, userId, teamId);

        StandupCreateDTO newStandup = new StandupCreateDTO();
        newStandup.setYesterday("Yesterday");
        newStandup.setToday("Today");
        newStandup.setBlockers("Blockers");

        User user = new User();
        user.setId(userId);
        user.setRole(Role.TEAM_MEMBER);

        Team team = new Team();
        team.setId(teamId);

        Standup savedStandup = new Standup();
        savedStandup.setId(1L);
        savedStandup.setYesterday(newStandup.getYesterday());
        savedStandup.setToday(newStandup.getToday());
        savedStandup.setBlockers(newStandup.getBlockers());
        savedStandup.setUser(user);
        savedStandup.setTeam(team);

        when(userService.getUserEntityById(userId)).thenReturn(user);
        when(teamService.getTeamEntityById(teamId)).thenReturn(team);
        when(standupRepository.save(any(Standup.class))).thenReturn(savedStandup);

        StandupDTO result = standupService.createStandup(newStandup);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(newStandup.getYesterday(), result.getYesterday());
        assertEquals(newStandup.getToday(), result.getToday());
        assertEquals(newStandup.getBlockers(), result.getBlockers());
    }

    @Test
    void createStandup_shouldThrowException_whenUserIsNotFound() {

        Long userId = 1L;

        setSecurityContext(Role.TEAM_MEMBER, userId, null);

        when(userService.getUserEntityById(userId)).thenThrow(new ResourceNotFoundException("User not found"));

        assertThrows(ResourceNotFoundException.class, () -> {
            standupService.createStandup(new StandupCreateDTO());
        });
    }

    @Test
    void getAllStandups_shouldReturnOwnStandupList_whenRoleIsTeamMember() {

        Long userId = 1L;
        Long teamId = 1L;

        setSecurityContext(Role.TEAM_MEMBER, userId, teamId);

        Standup standup1 = new Standup();
        standup1.setId(1L);
        standup1.setYesterday("Yesterday");
        standup1.setToday("Today");
        standup1.setBlockers("Blockers");

        Standup standup2 = new Standup();
        standup2.setId(2L);
        standup2.setYesterday("Yesterday");
        standup2.setToday("Today");
        standup2.setBlockers("Blockers");

        User user = new User();
        user.setId(userId);

        Team team = new Team();
        team.setId(teamId);

        standup1.setUser(user);
        standup1.setTeam(team);

        standup2.setUser(user);
        standup2.setTeam(team);

        when(standupRepository.findByUserId(userId)).thenReturn(List.of(standup1, standup2));

        List<StandupDTO> result = standupService.getAllStandups(null);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
    }

    @Test
    void getAllStandups_shouldReturnTeamStandupList_whenRoleIsTeamLead() {

        Long leadId = 1L;
        Long memberId = 2L;
        Long teamId = 1L;

        setSecurityContext(Role.TEAM_LEAD, leadId, teamId);

        Standup standup1 = new Standup();
        standup1.setId(1L);
        standup1.setYesterday("Yesterday");
        standup1.setToday("Today");
        standup1.setBlockers("Blockers");

        Standup standup2 = new Standup();
        standup2.setId(2L);
        standup2.setYesterday("Yesterday");
        standup2.setToday("Today");
        standup2.setBlockers("Blockers");

        User lead = new User();
        lead.setId(leadId);

        User member = new User();
        member.setId(memberId);

        Team team = new Team();
        team.setId(teamId);

        standup1.setUser(lead);
        standup1.setTeam(team);

        standup2.setUser(member);
        standup2.setTeam(team);

        when(standupRepository.findByTeamId(teamId)).thenReturn(List.of(standup1, standup2));

        List<StandupDTO> result = standupService.getAllStandups(null);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
    }

    @Test
    void getAllStandups_shouldReturnTeamStandupList_whenRoleIsManager() {

        Long managerId = 1L;
        Long leadId = 2L;
        Long memberId = 3L;
        Long teamId = 1L;

        setSecurityContext(Role.MANAGER, managerId, teamId);

        Standup standup1 = new Standup();
        standup1.setId(2L);
        standup1.setYesterday("Yesterday");
        standup1.setToday("Today");
        standup1.setBlockers("Blockers");

        Standup standup2 = new Standup();
        standup2.setId(3L);
        standup2.setYesterday("Yesterday");
        standup2.setToday("Today");
        standup2.setBlockers("Blockers");

        User lead = new User();
        lead.setId(leadId);

        User member = new User();
        member.setId(memberId);

        Team team = new Team();
        team.setId(teamId);

        standup1.setUser(lead);
        standup1.setTeam(team);

        standup2.setUser(member);
        standup2.setTeam(team);

        User manager = new User();
        manager.setId(managerId);

        when(standupRepository.findByTeamId(teamId)).thenReturn(List.of(standup1, standup2));

        List<StandupDTO> result = standupService.getAllStandups(null);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(2L, result.get(0).getId());
        assertEquals(3L, result.get(1).getId());
    }

    @Test
    void getAllStandups_shouldThrowException_whenRoleIsInvalid() {

        Long userId = 1L;
        Long teamId = 1L;

        setSecurityContext(Role.ADMIN, userId, teamId);

        assertThrows(AccessDeniedException.class, () -> {
            standupService.getAllStandups(null);
        });
    }

    @Test
    void getAllStandups_shouldReturnOwnStandupList_whenRoleIsTeamMemberAndDateProvided() {

        Long userId = 1L;
        Long teamId = 1L;

        setSecurityContext(Role.TEAM_MEMBER, userId, teamId);

        Standup standup1 = new Standup();
        standup1.setId(1L);
        standup1.setYesterday("Yesterday");
        standup1.setToday("Today");
        standup1.setBlockers("Blockers");
        standup1.setDate(LocalDate.now());

        User user = new User();
        user.setId(userId);

        Team team = new Team();
        team.setId(teamId);

        standup1.setUser(user);
        standup1.setTeam(team);

        when(standupRepository.findByUserIdAndDate(userId, LocalDate.now())).thenReturn(List.of(standup1));

        List<StandupDTO> result = standupService.getAllStandups(LocalDate.now());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    void getAllStandups_shouldReturnTeamStandupList_whenRoleIsTeamLeadAndDateProvided() {

        Long leadId = 1L;
        Long memberId = 2L;
        Long teamId = 1L;

        setSecurityContext(Role.TEAM_LEAD, leadId, teamId);

        Standup standup1 = new Standup();
        standup1.setId(1L);
        standup1.setYesterday("Yesterday");
        standup1.setToday("Today");
        standup1.setBlockers("Blockers");
        standup1.setDate(LocalDate.now());

        Standup standup2 = new Standup();
        standup2.setId(2L);
        standup2.setYesterday("Yesterday");
        standup2.setToday("Today");
        standup2.setBlockers("Blockers");
        standup2.setDate(LocalDate.now());

        User lead = new User();
        lead.setId(leadId);

        User member = new User();
        member.setId(memberId);

        Team team = new Team();
        team.setId(teamId);

        standup1.setUser(lead);
        standup1.setTeam(team);

        standup2.setUser(member);
        standup2.setTeam(team);

        when(standupRepository.findByTeamIdAndDate(teamId, LocalDate.now())).thenReturn(List.of(standup1, standup2));

        List<StandupDTO> result = standupService.getAllStandups(LocalDate.now());

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
    }

    @Test
    void getStandupById_shouldReturnStandupDto_whenTeamMemberViewsOwnStandup() {

        Long userId = 1L;
        Long teamId = 1L;

        setSecurityContext(Role.TEAM_MEMBER, userId, teamId);

        Standup standup = new Standup();
        standup.setId(1L);
        standup.setYesterday("Yesterday");
        standup.setToday("Today");
        standup.setBlockers("Blockers");

        User user = new User();
        user.setId(userId);

        Team team = new Team();
        team.setId(teamId);

        standup.setUser(user);
        standup.setTeam(team);

        when(standupRepository.findById(1L)).thenReturn(Optional.of(standup));

        StandupDTO result = standupService.getStandupById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Yesterday", result.getYesterday());
        assertEquals("Today", result.getToday());
        assertEquals("Blockers", result.getBlockers());
        assertEquals(userId, result.getUserId());
        assertEquals(teamId, result.getTeamId());
    }

    @Test
    void getStandupById_shouldThrowException_whenTeamMemberViewsOtherStandup() {

        Long userId = 2L;
        Long teamId = 1L;

        setSecurityContext(Role.TEAM_MEMBER, userId, teamId);

        Standup standup = new Standup();
        standup.setId(1L);
        standup.setYesterday("Yesterday");
        standup.setToday("Today");
        standup.setBlockers("Blockers");

        User user = new User();
        user.setId(1L);

        Team team = new Team();
        team.setId(teamId);

        standup.setUser(user);
        standup.setTeam(team);

        when(standupRepository.findById(1L)).thenReturn(Optional.of(standup));

        assertThrows(AccessDeniedException.class, () -> {
            standupService.getStandupById(1L);
        });
    }

    @Test
    void getStandupById_shouldReturnStandupDto_whenTeamLeadViewsTeamStandup() {

        Long userId1 = 1L;
        Long userId2 = 2L;
        Long teamId = 1L;

        setSecurityContext(Role.TEAM_LEAD, userId1, teamId);

        Standup standup = new Standup();
        standup.setId(1L);
        standup.setYesterday("Yesterday");
        standup.setToday("Today");
        standup.setBlockers("Blockers");

        User user = new User();
        user.setId(userId2);

        Team team = new Team();
        team.setId(teamId);

        standup.setUser(user);
        standup.setTeam(team);

        when(standupRepository.findById(1L)).thenReturn(Optional.of(standup));

        StandupDTO result = standupService.getStandupById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Yesterday", result.getYesterday());
        assertEquals("Today", result.getToday());
        assertEquals("Blockers", result.getBlockers());
        assertEquals(userId2, result.getUserId());
        assertNotEquals(userId1, result.getUserId());
        assertEquals(teamId, result.getTeamId());
    }

    @Test
    void getStandupById_shouldThrowException_whenStandupNotFound() {

        Long userId = 1L;

        setSecurityContext(Role.TEAM_MEMBER, userId, null);

        when(standupRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            standupService.getStandupById(1L);
        });
    }

    @Test
    void updateStandup_shouldReturnUpdatedStandupDto_whenOwnerUpdates() {

        Long userId = 1L;
        Long teamId = 1L;
        Long standupId = 1L;

        setSecurityContext(Role.TEAM_MEMBER, userId, teamId);

        StandupUpdateDTO updateStandupDto = new StandupUpdateDTO();
        updateStandupDto.setYesterday("Updated Yesterday");
        updateStandupDto.setToday("Updated Today");
        updateStandupDto.setBlockers("Updated Blockers");

        Standup standup = new Standup();
        standup.setId(standupId);
        standup.setYesterday("Yesterday");
        standup.setToday("Today");
        standup.setBlockers("Blockers");

        User user = new User();
        user.setId(userId);

        Team team = new Team();
        team.setId(teamId);

        standup.setUser(user);
        standup.setTeam(team);

        when(standupRepository.findById(standupId)).thenReturn(Optional.of(standup));
        when(standupRepository.save(any(Standup.class))).thenReturn(standup);

        StandupDTO result = standupService.updateStandup(standupId, updateStandupDto);

        ArgumentCaptor<Standup> captor = ArgumentCaptor.forClass(Standup.class);
        verify(standupRepository).save(captor.capture());
        Standup updatedStandup = captor.getValue();
        assertEquals(standupId, updatedStandup.getId());
        assertEquals("Updated Yesterday", updatedStandup.getYesterday());
        assertEquals("Updated Today", updatedStandup.getToday());
        assertEquals("Updated Blockers", updatedStandup.getBlockers());

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Updated Yesterday", result.getYesterday());
        assertEquals("Updated Today", result.getToday());
        assertEquals("Updated Blockers", result.getBlockers());
        assertEquals(userId, result.getUserId());
        assertEquals(teamId, result.getTeamId());
    }

    @Test
    void updateStandup_shouldThrowException_whenNonOwnerUpdates() {

        Long userId1 = 1L;
        Long userId2 = 2L;
        Long standupId = 1L;

        setSecurityContext(Role.TEAM_MEMBER, userId1, null);

        StandupUpdateDTO updateStandupDto = new StandupUpdateDTO();
        updateStandupDto.setYesterday("Updated Yesterday");
        updateStandupDto.setToday("Updated Today");
        updateStandupDto.setBlockers("Updated Blockers");

        Standup standup = new Standup();
        standup.setId(standupId);
        standup.setYesterday("Yesterday");
        standup.setToday("Today");
        standup.setBlockers("Blockers");

        User user = new User();
        user.setId(userId2);

        standup.setUser(user);

        when(standupRepository.findById(standupId)).thenReturn(Optional.of(standup));

        assertThrows(AccessDeniedException.class, () -> {
            standupService.updateStandup(standupId, updateStandupDto);
        });
    }

    @Test
    void updateStandup_shouldThrowException_whenStandupNotFound() {

        Long userId = 1L;
        Long teamId = 1L;
        Long standupId = 1L;

        setSecurityContext(Role.TEAM_MEMBER, userId, teamId);

        when(standupRepository.findById(standupId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            standupService.updateStandup(standupId, new StandupUpdateDTO());
        });
    }

    @Test
    void deleteStandup_shouldDeleteStandup_whenOwnerDeletes() {

        Long userId = 1L;
        Long teamId = 1L;
        Long standupId = 1L;

        setSecurityContext(Role.TEAM_MEMBER, userId, teamId);

        Standup standup = new Standup();
        standup.setId(standupId);
        standup.setYesterday("Yesterday");
        standup.setToday("Today");
        standup.setBlockers("Blockers");

        User user = new User();
        user.setId(userId);

        Team team = new Team();
        team.setId(teamId);

        standup.setUser(user);
        standup.setTeam(team);

        when(standupRepository.findById(standupId)).thenReturn(Optional.of(standup));

        standupService.deleteStandup(standupId);

        verify(standupRepository).delete(standup);
    }

    @Test
    void deleteStandup_shouldThrowException_whenNonOwnerDeletes() {

        Long userId1 = 1L;
        Long userId2 = 2L;
        Long standupId = 1L;

        setSecurityContext(Role.TEAM_MEMBER, userId1, null);

        Standup standup = new Standup();
        standup.setId(standupId);
        standup.setYesterday("Yesterday");
        standup.setToday("Today");
        standup.setBlockers("Blockers");

        User user = new User();
        user.setId(userId2);

        standup.setUser(user);

        when(standupRepository.findById(standupId)).thenReturn(Optional.of(standup));

        assertThrows(AccessDeniedException.class, () -> {
            standupService.deleteStandup(standupId);
        });
    }

    @Test
    void deleteStandup_shouldThrowException_whenStandupNotFound() {

        Long userId = 1L;
        Long teamId = 1L;
        Long standupId = 1L;

        setSecurityContext(Role.TEAM_MEMBER, userId, teamId);

        when(standupRepository.findById(standupId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            standupService.deleteStandup(standupId);
        });
    }

    private void setSecurityContext(Role role, Long userId, Long teamId) {

        CustomUserDetails mockUser = new CustomUserDetails(
                userId, "test@test.com", "password", role.name(), teamId,
                List.of(new SimpleGrantedAuthority("ROLE_" + role.name()))
        );

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                mockUser, null, mockUser.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}