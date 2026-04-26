package com.teamsync.teamsync.controller;

import com.teamsync.teamsync.dto.ReportDTO;
import com.teamsync.teamsync.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'TEAM_LEAD')")
    public ResponseEntity<List<ReportDTO>> getReports(@RequestParam Long teamId, @RequestParam(required = false) LocalDate startDate, @RequestParam(required = false) LocalDate endDate) {
        return ResponseEntity.ok(reportService.getReportByTeamId(teamId, startDate, endDate));
    }
}
