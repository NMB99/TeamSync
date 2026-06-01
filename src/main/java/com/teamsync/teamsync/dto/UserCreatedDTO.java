package com.teamsync.teamsync.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCreatedDTO {

    private Long id;
    private String fullName;
    private String email;
    private String role;
    private String generatedPassword;

}
