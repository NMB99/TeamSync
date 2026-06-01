package com.teamsync.teamsync.controller;

import com.teamsync.teamsync.dto.TeamCreateDTO;
import com.teamsync.teamsync.dto.TeamDTO;
import com.teamsync.teamsync.dto.TeamUpdateDTO;
import com.teamsync.teamsync.service.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Teams", description = "Team management — MANAGER creates and manages, all roles can view")
@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @Operation(summary = "Create team", description = "Requires MANAGER role")
    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<TeamDTO> createTeam(@RequestBody @Valid TeamCreateDTO newTeamDTO) {
        TeamDTO newTeam = teamService.createTeam(newTeamDTO);
        return new ResponseEntity<>(newTeam, HttpStatus.CREATED);
    }

    @Operation(summary = "Get all teams", description = "Requires MANAGER, TEAM_LEAD, or TEAM_MEMBER role")
    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'TEAM_LEAD', 'TEAM_MEMBER')")
    public ResponseEntity<List<TeamDTO>> getAllTeams() {
        return ResponseEntity.ok(teamService.getAllTeams());
    }

    @Operation(summary = "Get team by ID", description = "Requires MANAGER, TEAM_LEAD, or TEAM_MEMBER role")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'TEAM_LEAD', 'TEAM_MEMBER')")
    public ResponseEntity<TeamDTO> getTeamById(@PathVariable Long id) {
        TeamDTO team = teamService.getTeamById(id);
        return ResponseEntity.ok(team);
    }

    @Operation(summary = "Update team", description = "Requires MANAGER role")
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<TeamDTO> updateTeam(@PathVariable Long id, @RequestBody @Valid TeamUpdateDTO team) {
        return ResponseEntity.ok(teamService.updateTeam(id, team));
    }

    @Operation(summary = "Delete team", description = "Requires MANAGER role")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long id) {
        teamService.deleteTeam(id);
        return ResponseEntity.noContent().build();
    }
  
}
