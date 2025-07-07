package com.teamsync.teamsync.service;

import com.teamsync.teamsync.dto.StandupDTO;
import com.teamsync.teamsync.entity.Standup;
import com.teamsync.teamsync.repository.StandupRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StandupService {

    private final StandupRepository standupRepository;

    public StandupService(StandupRepository standupRepository) {
        this.standupRepository = standupRepository;
    }

    public Standup createStandup(Standup standup) {
        return standupRepository.save(standup);
    }

    public List<StandupDTO> getAllStandups() {
        List<Standup> standups = standupRepository.findAll();
        return standups.stream().map(this::createStandupDTO).toList();
    }

    public StandupDTO getStandupById(long id) {
        Standup standup = standupRepository.findById(id).orElse(null);
        if (standup != null) {
            return createStandupDTO(standup);
        }
        return null;
    }

    public Standup updateStandup(long id, Standup standup) {
        Standup exists = standupRepository.findById(id).orElse(null);
        if (exists != null) {
            exists.setDate(standup.getDate());
            exists.setYesterday(standup.getYesterday());
            exists.setToday(standup.getToday());
            exists.setBlockers(standup.getBlockers());
            exists.setUser(standup.getUser());
            exists.setTeam(standup.getTeam());
            return standupRepository.save(exists);
        }
        return null;
    }

    public Standup deleteStandup(long id) {
        Standup standup = standupRepository.findById(id).orElse(null);
        if (standup != null) {
            standupRepository.delete(standup);
        }
        return standup;
    }

    private StandupDTO createStandupDTO(Standup standup) {
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
