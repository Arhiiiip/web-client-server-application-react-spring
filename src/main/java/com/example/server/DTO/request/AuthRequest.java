package com.example.server.DTO.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Email;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class AuthRequest {

    private String username;
    private String password;
}
