package com.teamsync.teamsync.dto;

import com.teamsync.teamsync.enums.TeamCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamUpdateDTO {

    private String name;
    private String description;
    private TeamCategory category;

}
