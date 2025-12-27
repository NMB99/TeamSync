package com.teamsync.teamsync.dto;

import lombok.Data;

@Data
public class AuthResponse {

    private String token;
    private String role;
    private String token_type = "Bearer";


    public AuthResponse(String token, String role) {
        this.token = token;
        this.role = role;
    }

}
