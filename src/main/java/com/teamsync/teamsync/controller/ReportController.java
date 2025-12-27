package com.teamsync.teamsync.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ReportController {

    @GetMapping("/reports")
    public ResponseEntity<?> getReports() {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
