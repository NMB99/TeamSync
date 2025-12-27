package com.teamsync.teamsync.service;

import com.teamsync.teamsync.dto.TeamCreateDTO;
import com.teamsync.teamsync.dto.TeamDTO;
import com.teamsync.teamsync.dto.TeamUpdateDTO;
import com.teamsync.teamsync.entity.Team;
import com.teamsync.teamsync.entity.User;
import com.teamsync.teamsync.enums.Role;
import com.teamsync.teamsync.exception.ResourceNotFoundException;
import com.teamsync.teamsync.repository.TeamRepository;
import com.teamsync.teamsync.repository.UserRepository;
import com.teamsync.teamsync.security.CustomUserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamService {

    private final TeamRepository teamRepository;

    public TeamService(TeamRepository teamRepository, UserRepository userRepository) {
        this.teamRepository = teamRepository;
    }

    public TeamDTO createTeam(TeamCreateDTO teamDTO) {
        Team team = new Team();
        team.setName(teamDTO.getName());
        team.setDescription(teamDTO.getDescription());
        team.setCategory(teamDTO.getCategory());

        Team savedTeam = teamRepository.save(team);
        return convertTeamToDTO(savedTeam);
    }

    public List<TeamDTO> getAllTeams() {
        CustomUserDetails currentUser = (CustomUserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        System.out.println("ROLE: " + currentUser.getRoleEnum());
        return teamRepository.findAll()
                .stream().map(this::convertTeamToDTO)
                .toList();
    }

    public Team getTeamEntityById(Long id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team with \"id: " + id +"\" not found"));
    }

    public TeamDTO getTeamById(Long id) {
        CustomUserDetails currentUser = (CustomUserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        Role role = currentUser.getRoleEnum();

        if (role == Role.TEAM_MEMBER || role == Role.TEAM_LEAD) {
            if (currentUser.getTeam() == null || !currentUser.getTeam().getId().equals(id)) {
                throw new ResourceNotFoundException("Access denied: You can only view your team's information.");
            }
        }

        Team team = getTeamEntityById(id);
        return convertTeamToDTO(team);
    }

    public TeamDTO updateTeam(Long id, TeamUpdateDTO team) {
        Team updatedTeam = getTeamEntityById(id);

        if (team.getName() != null) {
            updatedTeam.setName(team.getName());
        }
        if (team.getDescription() != null) {
            updatedTeam.setDescription(team.getDescription());
        }
        if (team.getCategory() != null) {
            updatedTeam.setCategory(team.getCategory());
        }
        teamRepository.save(updatedTeam);

        return convertTeamToDTO(updatedTeam);
    }

    public void deleteTeam(Long id) {
        getTeamEntityById(id);
        teamRepository.deleteById(id);
    }

    private TeamDTO convertTeamToDTO(Team team) {
        TeamDTO teamDTO = new TeamDTO();
        teamDTO.setId(team.getId());
        teamDTO.setName(team.getName());
        teamDTO.setDescription(team.getDescription());
        teamDTO.setCategory(
                team.getCategory() != null ? team.getCategory().name() : null
        );

        List<String> members = team.getUsers() != null ?
                team.getUsers()
                        .stream()
                        .map(User::getFullName)
                        .toList()
                : List.of();

        teamDTO.setMembers(members);
        return teamDTO;
    }
}
