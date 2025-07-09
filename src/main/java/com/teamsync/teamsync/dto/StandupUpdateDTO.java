package com.teamsync.teamsync.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StandupUpdateDTO {

    @NotBlank
    private String yesterday;

    @NotBlank
    private String today;

    private String blockers;
}
