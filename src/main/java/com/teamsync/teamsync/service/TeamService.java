package com.teamsync.teamsync.service;

import com.teamsync.teamsync.entity.Team;
import com.teamsync.teamsync.repository.TeamRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamService {

    private final TeamRepository teamRepository;

    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    public Team createTeam(Team team) {
        return teamRepository.save(team);
    }

    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    public Team getTeamById(Long id) {
        return teamRepository.findById(id).orElse(null);
    }

    public Team updateTeam(Long id, Team team) {
        Team updatedTeam = teamRepository.findById(id).orElse(null);
        if (updatedTeam != null) {
            if (team.getName() != null) {
                updatedTeam.setName(team.getName());
            }
            if (team.getDescription() != null) {
                updatedTeam.setDescription(team.getDescription());
            }
            teamRepository.save(updatedTeam);
        }
        return updatedTeam;
    }

    public Team deleteTeam(Long id) {
        Team team = teamRepository.findById(id).orElse(null);
        if (team != null) {
            teamRepository.delete(team);
        }
        return team;
    }
}
