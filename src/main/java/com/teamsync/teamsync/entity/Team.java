package com.teamsync.teamsync.entity;

import com.teamsync.teamsync.enums.TeamCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    @Enumerated(EnumType.STRING)
    private TeamCategory category;

    @OneToMany(mappedBy = "team")
    private List<User> users = new ArrayList<>();

    @OneToMany(mappedBy = "team")
    private List<Standup> standups =  new ArrayList<>();

}
