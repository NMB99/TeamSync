package com.teamsync.teamsync.controller;

import com.teamsync.teamsync.dto.StandupCreateDTO;
import com.teamsync.teamsync.dto.StandupDTO;
import com.teamsync.teamsync.dto.StandupUpdateDTO;
import com.teamsync.teamsync.service.StandupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Standups", description = "Daily standup submissions and retrieval")
@RestController
@RequestMapping("/api/standups")
public class StandupController {

    private final StandupService standupService;

    public StandupController(StandupService standupService) {
        this.standupService = standupService;
    }

    @Operation(summary = "Submit standup", description = "Any authenticated user — ownership enforced in service layer")
    @PostMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'TEAM_LEAD', 'TEAM_MEMBER')")
    public ResponseEntity<StandupDTO> createStandup(@RequestBody @Valid StandupCreateDTO standup) {
        StandupDTO newStandup = standupService.createStandup(standup);
        return new ResponseEntity<>(newStandup, HttpStatus.CREATED);
    }

    @Operation(summary = "Get all standups", description = "Filtered by date if provided. Results scoped to the caller's role and team.")
    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'TEAM_LEAD', 'TEAM_MEMBER')")
    public ResponseEntity<List<StandupDTO>> getAllStandups(@RequestParam(required = false) LocalDate date) {
        return new ResponseEntity<>(standupService.getAllStandups(date), HttpStatus.OK);
    }

    @Operation(summary = "Get standup by ID", description = "Returns a single standup entry")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'TEAM_LEAD', 'TEAM_MEMBER')")
    public ResponseEntity<StandupDTO> getStandupById(@PathVariable Long id) {
        StandupDTO standup = standupService.getStandupById(id);
        return new ResponseEntity<>(standup, HttpStatus.OK);
    }

    @Operation(summary = "Update standup", description = "Only the owner can update their own standup")
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'TEAM_LEAD', 'TEAM_MEMBER')")
    public ResponseEntity<StandupDTO> updateStandup(@PathVariable Long id, @RequestBody @Valid StandupUpdateDTO standup) {
        StandupDTO updated = standupService.updateStandup(id, standup);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @Operation(summary = "Delete standup", description = "Only the owner can delete their own standup")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'TEAM_LEAD', 'TEAM_MEMBER')")
    public ResponseEntity<Void> deleteStandup(@PathVariable Long id) {
        standupService.deleteStandup(id);
        return ResponseEntity.noContent().build();
        }
}
