package com.teamsync.teamsync.service;

import com.teamsync.teamsync.dto.StandupCreateDTO;
import com.teamsync.teamsync.dto.StandupDTO;
import com.teamsync.teamsync.dto.StandupUpdateDTO;
import com.teamsync.teamsync.entity.Standup;
import com.teamsync.teamsync.entity.Team;
import com.teamsync.teamsync.enums.Role;
import com.teamsync.teamsync.exception.BadRequestException;
import com.teamsync.teamsync.exception.ResourceNotFoundException;
import com.teamsync.teamsync.repository.StandupRepository;
import com.teamsync.teamsync.security.CustomUserDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;


@Service
public class StandupService {

    private final StandupRepository standupRepository;
    private final UserService userService;
    private final TeamService teamService;

    public StandupService(StandupRepository standupRepository, UserService userService, TeamService teamService) {
        this.standupRepository = standupRepository;
        this.userService = userService;
        this.teamService = teamService;
    }

    @Transactional
    public StandupDTO createStandup(StandupCreateDTO standupDTO) {
        CustomUserDetails currentUser = (CustomUserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        Standup standup = new Standup();
        standup.setDate(LocalDate.now());
        standup.setYesterday(standupDTO.getYesterday());
        standup.setToday(standupDTO.getToday());
        standup.setBlockers(standupDTO.getBlockers());
        standup.setUser(userService.getUserEntityById(currentUser.getId()));

        Team team = teamService.getTeamEntityById(currentUser.getTeamId());
        standup.setTeam(team);
        return convertStandupToDTO(standupRepository.save(standup));
    }

    public List<StandupDTO> getAllStandups(Long teamId, LocalDate date) {
        CustomUserDetails currentUser = (CustomUserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        Long userId = currentUser.getId();
        Role role = currentUser.getRoleEnum();
        List<Standup> standups;

        if (role == Role.TEAM_MEMBER) {
            standups = standupRepository.findByUserId(userId);
        }
        else {
            if (teamId != null && date != null) {
                standups = standupRepository.findByTeamIdAndDate(teamId, date);
            } else if (teamId != null) {
                standups = standupRepository.findByTeamId(teamId);
            } else {
                throw new IllegalArgumentException("team id is required to fetch standups.");
            }
        }

        return standups.stream().map(this::convertStandupToDTO).toList();
    }

    public StandupDTO getStandupById(Long id) {
        CustomUserDetails currentUser = (CustomUserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        Standup standup = standupRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Standup with id " + id + " not found.")
                );

        if (!standup.getUser().getId().equals(currentUser.getId())) {
            throw new BadRequestException("You can only view your own standups.");
        }

        return convertStandupToDTO(standup);
    }

    @Transactional
    public StandupDTO updateStandup(Long id, StandupUpdateDTO standup) {
        CustomUserDetails currentUser = (CustomUserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        Standup exists = standupRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Standup with id " + id + " not found.")
                );

        if (!exists.getUser().getId().equals(currentUser.getId())) {
            throw new BadRequestException("You can only update your own standups.");
        }

        if (standup.getYesterday() != null) {
            exists.setYesterday(standup.getYesterday());
        }
        if (standup.getToday() != null) {
            exists.setToday(standup.getToday());
        }
        if (standup.getBlockers() != null) {
            exists.setBlockers(standup.getBlockers());
        }
        standupRepository.save(exists);

        return convertStandupToDTO(exists);
    }

    @Transactional
    public void deleteStandup(Long id) {
        CustomUserDetails currentUser = (CustomUserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        Standup standup = standupRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Standup with id " + id + " not found.")
                );

        if (!standup.getUser().getId().equals(currentUser.getId())) {
            throw new BadRequestException("You can only delete your own standups.");
        }

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
