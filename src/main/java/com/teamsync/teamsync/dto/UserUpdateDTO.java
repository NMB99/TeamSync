package com.teamsync.teamsync.dto;

import com.teamsync.teamsync.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDTO {

    private String fullName;
    private String email;
    private Long teamId;
    private Role role;

}
