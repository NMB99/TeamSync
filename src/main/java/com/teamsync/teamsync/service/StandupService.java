package com.teamsync.teamsync.service;

import com.teamsync.teamsync.dto.StandupCreateDTO;
import com.teamsync.teamsync.dto.StandupDTO;
import com.teamsync.teamsync.dto.StandupUpdateDTO;
import com.teamsync.teamsync.entity.Standup;
import com.teamsync.teamsync.repository.StandupRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class StandupService {

    private final StandupRepository standupRepository;
    private final TeamService teamService;
    private final UserService userService;

    public StandupService(StandupRepository standupRepository, TeamService teamService, UserService userService) {
        this.standupRepository = standupRepository;
        this.teamService = teamService;
        this.userService = userService;
    }

    public StandupDTO createStandup(StandupCreateDTO standupDTO) {
        Standup standup = new Standup();
        standup.setDate(
                standupDTO.getDate() != null ? standupDTO.getDate() : LocalDate.now()
        );
        standup.setYesterday(standupDTO.getYesterday());
        standup.setToday(standupDTO.getToday());
        standup.setUser(userService.getUserEntityById(standupDTO.getUserId()));
        standup.setTeam(teamService.getTeamEntityById(standupDTO.getTeamId()));
        return convertStandupToDTO(standupRepository.save(standup));
    }

    public List<StandupDTO> getAllStandups() {
        List<Standup> standups = standupRepository.findAll();
        return standups.stream().map(this::convertStandupToDTO).toList();
    }

    public StandupDTO getStandupById(long id) {
        Standup standup = standupRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Standup with id " + id + " not found"));

        return convertStandupToDTO(standup);
    }

    public StandupDTO updateStandup(long id, StandupUpdateDTO standup) {
        Standup exists = standupRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Standup with id " + id + " not found"));

        exists.setYesterday(standup.getYesterday());
        exists.setToday(standup.getToday());
        exists.setBlockers(standup.getBlockers());
        standupRepository.save(exists);

        return convertStandupToDTO(exists);
    }

    public void deleteStandup(long id) {
        Standup standup = standupRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Standup with id " + id + " not found"));

        standupRepository.delete(standup);
    }

    private StandupDTO convertStandupToDTO(Standup standup) {
        StandupDTO standupDTO = new StandupDTO();
        standupDTO.setId(standup.getId());
        standupDTO.setDate(standup.getDate());
        standupDTO.setYesterday(standup.getYesterday());
        standupDTO.setToday(standup.getToday());
        standupDTO.setBlockers(standup.getBlockers());
        standupDTO.setUserId(standup.getUser().getId());
        standupDTO.setTeamId(standup.getTeam().getId());
        return standupDTO;
    }
}
