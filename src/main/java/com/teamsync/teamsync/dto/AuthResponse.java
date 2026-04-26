package com.teamsync.teamsync.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AuthResponse {

    private String token;
    private String role;

    @JsonProperty("token_type")
    private String tokenType = "Bearer";


    public AuthResponse(String token, String role) {
        this.token = token;
        this.role = role;
    }

}
