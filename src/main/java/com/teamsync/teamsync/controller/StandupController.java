package com.teamsync.teamsync.controller;

import com.teamsync.teamsync.dto.StandupDTO;
import com.teamsync.teamsync.entity.Standup;
import com.teamsync.teamsync.service.StandupService;
import jakarta.persistence.EntityNotFoundException;
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
    public ResponseEntity<Standup> createStandup(@RequestBody Standup standup) {
        Standup newStandup = standupService.createStandup(standup);
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

    @PutMapping("/{id}")
    public ResponseEntity<Standup> updateStandup(@PathVariable long id, @RequestBody Standup standup) {
        try {
            Standup updated = standupService.updateStandup(id, standup);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        }
        catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Standup> deleteStandup(@PathVariable long id) {
        try {
            Standup standup = standupService.deleteStandup(id);
            return new ResponseEntity<>(standup, HttpStatus.OK);
        }
        catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
