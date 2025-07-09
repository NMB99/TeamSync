package com.teamsync.teamsync.dto;

import com.teamsync.teamsync.enums.TeamCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamUpdateDTO {

    @NotBlank
    private String name;

    private String description;

    @NotNull
    private TeamCategory category;
}
