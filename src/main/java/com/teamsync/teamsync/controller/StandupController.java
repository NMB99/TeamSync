package com.teamsync.teamsync.controller;

import com.teamsync.teamsync.dto.StandupCreateDTO;
import com.teamsync.teamsync.dto.StandupDTO;
import com.teamsync.teamsync.dto.StandupUpdateDTO;
import com.teamsync.teamsync.service.StandupService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/standups")
public class StandupController {

    private final StandupService standupService;

    public StandupController(StandupService standupService) {
        this.standupService = standupService;
    }

    @PostMapping
    public ResponseEntity<StandupDTO> createStandup(@RequestBody @Valid StandupCreateDTO standup) {
        StandupDTO newStandup = standupService.createStandup(standup);
        return new ResponseEntity<>(newStandup, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<StandupDTO>> getAllStandups() {
        return new ResponseEntity<>(standupService.getAllStandups(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StandupDTO> getStandupById(@PathVariable long id) {
        try {
            StandupDTO standup = standupService.getStandupById(id);
            return new ResponseEntity<>(standup, HttpStatus.OK);
        }
        catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<StandupDTO> updateStandup(@PathVariable long id, @RequestBody @Valid StandupUpdateDTO standup) {
        try {
            StandupDTO updated = standupService.updateStandup(id, standup);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        }
        catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStandup(@PathVariable long id) {
        try {
            standupService.deleteStandup(id);
            return ResponseEntity.noContent().build();
        }
        catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
