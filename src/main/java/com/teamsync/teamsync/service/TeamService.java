package com.teamsync.teamsync.service;

import com.teamsync.teamsync.dto.TeamCreateDTO;
import com.teamsync.teamsync.dto.TeamDTO;
import com.teamsync.teamsync.dto.TeamUpdateDTO;
import com.teamsync.teamsync.entity.Team;
import com.teamsync.teamsync.entity.User;
import com.teamsync.teamsync.repository.TeamRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamService {

    private final TeamRepository teamRepository;

    public TeamService(TeamRepository teamRepository) {
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
        return teamRepository.findAll()
                .stream().map(this::convertTeamToDTO)
                .toList();
    }

    public Team getTeamEntityById(Long id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Team not found"));
    }

    public TeamDTO getTeamById(Long id) {
        Team team = getTeamEntityById(id);
        return convertTeamToDTO(team);
    }

    public TeamDTO updateTeam(Long id, TeamUpdateDTO team) {
        Team updatedTeam = teamRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Team with id " + id + " not found"));

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
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Team with id " + id + " not found"));
        teamRepository.delete(team);
    }

    private TeamDTO convertTeamToDTO(Team team) {
        TeamDTO teamDTO = new TeamDTO();
        teamDTO.setId(team.getId());
        teamDTO.setName(team.getName());
        teamDTO.setDescription(team.getDescription());
        teamDTO.setCategory(team.getCategory().toString());

        List<String> members = team.getUsers()
                .stream()
                .map(User::getFullName)
                .toList();

        teamDTO.setMembers(members);
        return teamDTO;
    }
}
