package com.teamsync.teamsync.controller;

import com.teamsync.teamsync.dto.StandupCreateDTO;
import com.teamsync.teamsync.dto.StandupDTO;
import com.teamsync.teamsync.dto.StandupUpdateDTO;
import com.teamsync.teamsync.service.StandupService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/standups")
public class StandupController {

    private final StandupService standupService;

    public StandupController(StandupService standupService) {
        this.standupService = standupService;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<StandupDTO> createStandup(@RequestBody @Valid StandupCreateDTO standup) {
        StandupDTO newStandup = standupService.createStandup(standup);
        return new ResponseEntity<>(newStandup, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<StandupDTO>> getAllStandups(@RequestParam(required = false) Long teamId, @RequestParam(required = false) LocalDate date) {
        return new ResponseEntity<>(standupService.getAllStandups(teamId, date), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<StandupDTO> getStandupById(@PathVariable Long id) {
        StandupDTO standup = standupService.getStandupById(id);
        return new ResponseEntity<>(standup, HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<StandupDTO> updateStandup(@PathVariable Long id, @RequestBody @Valid StandupUpdateDTO standup) {
        StandupDTO updated = standupService.updateStandup(id, standup);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteStandup(@PathVariable Long id) {
        standupService.deleteStandup(id);
        return ResponseEntity.noContent().build();
        }
}
