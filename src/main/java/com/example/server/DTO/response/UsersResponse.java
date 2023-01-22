package com.example.server.DTO.response;

import com.example.server.DTO.utility.Point;
import com.example.server.entities.User;
import lombok.Data;

import java.util.List;

@Data
public class UsersResponse {
    private final List<User> users;
}
