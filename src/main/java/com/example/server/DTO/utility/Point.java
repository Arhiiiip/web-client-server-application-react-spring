package com.example.server.DTO.utility;

import com.example.server.entities.User;
import lombok.*;

@Data
@NoArgsConstructor
public class Point {
    private boolean hitResult;
    private Coordinates coordinates;
    private String currentTime;
    private String id;
    private String executionTime;
    private String user;
    private String idUser;
}
