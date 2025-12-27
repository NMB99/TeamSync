package com.teamsync.teamsync.controller;

import com.teamsync.teamsync.dto.TeamCreateDTO;
import com.teamsync.teamsync.dto.TeamDTO;
import com.teamsync.teamsync.dto.TeamUpdateDTO;
import com.teamsync.teamsync.exception.ResourceNotFoundException;
import com.teamsync.teamsync.service.TeamService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<TeamDTO> createTeam(@RequestBody @Valid TeamCreateDTO newTeamDTO) {
        TeamDTO newTeam = teamService.createTeam(newTeamDTO);
        return new ResponseEntity<>(newTeam, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<TeamDTO>> getAllTeams() {
        return ResponseEntity.ok(teamService.getAllTeams());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEAM_LEAD', 'TEAM_MEMBER')")
    public ResponseEntity<TeamDTO> getTeamById(@PathVariable Long id) {
        try {
            TeamDTO team = teamService.getTeamById(id);
            return ResponseEntity.ok(team);
        }
        catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<TeamDTO> updateTeam(@PathVariable Long id, @RequestBody @Valid TeamUpdateDTO team) {
        try {
            return ResponseEntity.ok(teamService.updateTeam(id, team));
        }
        catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long id) {
        try {
            teamService.deleteTeam(id);
            return ResponseEntity.noContent().build();
        }
        catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
  
}
