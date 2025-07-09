package com.teamsync.teamsync.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StandupCreateDTO {

    private LocalDate date;

    @NotBlank
    private String yesterday;

    @NotBlank
    private String today;

    @NotNull
    private Long userId;

    @NotNull
    private Long teamId;

}
