package com.example.server.DTO.response;

import com.example.server.entities.Roles;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String username;
    private String token;
    private Roles role;
}
