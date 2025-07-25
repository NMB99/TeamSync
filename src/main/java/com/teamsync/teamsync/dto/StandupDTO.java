package com.teamsync.teamsync.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StandupDTO {

    private Long id;
    private LocalDate date;
    private String yesterday;
    private String today;
    private String blockers;
    private Long userId;
    private Long teamId;

}
