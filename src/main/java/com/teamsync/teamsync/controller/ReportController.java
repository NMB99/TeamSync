package com.teamsync.teamsync.controller;

import com.teamsync.teamsync.dto.ReportDTO;
import com.teamsync.teamsync.service.ReportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/reports")
    public ResponseEntity<List<ReportDTO>> getReports(@RequestParam Long teamId) {
        List<ReportDTO> reports = reportService.getReportByTeam_Id(teamId);
        return ResponseEntity.ok(reports);
    }
}
