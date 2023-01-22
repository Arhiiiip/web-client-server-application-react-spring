package com.example.server.converter;

import com.example.server.DTO.request.AuthRequest;
import com.example.server.entities.User;
import org.springframework.stereotype.Service;

@Service
public class UserFormsConverter {

    public User convertAuthToEntity(AuthRequest authRequest) {
        User user = new User();
        user.setUsername(authRequest.getUsername());
        user.setPassword(authRequest.getPassword());
        return user;
    }

    public User convertUserToEntity(User request) {
        User user = new User();
        user.setId(request.getId());
        user.setUsername(request.getUsername());
        user.setRole(request.getRole());
        return user;
    }
}
