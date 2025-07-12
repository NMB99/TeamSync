package com.teamsync.teamsync.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StandupUpdateDTO {

    private String yesterday;
    private String today;
    private String blockers;

}
