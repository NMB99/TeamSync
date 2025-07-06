package com.teamsync.teamsync.controller;

import com.teamsync.teamsync.service.TeamService;

public class StandupController {

    private final TeamService teamService;

    public StandupController(TeamService teamService) {
        this.teamService = teamService;
    }
}
