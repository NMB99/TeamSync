package com.teamsync.teamsync.service;

import com.teamsync.teamsync.dto.ReportDTO;
import com.teamsync.teamsync.entity.Standup;
import com.teamsync.teamsync.repository.StandupRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final StandupRepository standupRepository;

    public ReportService(StandupRepository standupRepository) {
        this.standupRepository = standupRepository;
    }

    public List<ReportDTO> getReportByTeam_Id(long teamId) {
        List<Standup> standups = standupRepository.findAllByTeamId(teamId);
        return standups.stream()
                .map(this::convertToReportDTO)
                .collect(Collectors.toList());
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
