package com.teamsync.teamsync.service;

import com.teamsync.teamsync.dto.ReportDTO;
import com.teamsync.teamsync.entity.Standup;
import com.teamsync.teamsync.repository.StandupRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReportService {

    private final StandupRepository standupRepository;

    public ReportService(StandupRepository standupRepository) {
        this.standupRepository = standupRepository;
    }

    @Transactional(readOnly = true)
    public List<ReportDTO> getReportByTeamId(long teamId, LocalDate startDate, LocalDate endDate) {
        List<Standup> standups;

        if (startDate != null && endDate != null) {
            standups = standupRepository.findByTeamIdAndDateBetween(teamId, startDate, endDate);
        }
        else {
            standups = standupRepository.findByTeamId(teamId);
        }

        return standups.stream()
                .map(this::convertToReportDTO)
                .toList();
    }

    private ReportDTO convertToReportDTO(Standup standup) {
        return new ReportDTO(
                standup.getUser().getId(),
                standup.getUser().getFullName(),
                standup.getDate(),
                standup.getYesterday(),
                standup.getToday(),
                standup.getBlockers()
        );
    }

}
