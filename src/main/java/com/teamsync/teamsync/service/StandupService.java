package com.teamsync.teamsync.service;

import org.springframework.stereotype.Service;

@Service
public class StandupService {

    private final TeamService teamService;

    public StandupService(TeamService teamService) {
        this.teamService = teamService;
    }
}
