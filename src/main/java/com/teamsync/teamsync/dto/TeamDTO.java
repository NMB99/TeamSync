package com.teamsync.teamsync.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamDTO {

    private Long id;
    private String name;
    private String description;
    private List<String> members;

}
