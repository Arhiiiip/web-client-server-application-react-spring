package com.example.server.DTO.response;

import com.example.server.DTO.utility.Point;
import lombok.Data;

import java.util.List;

@Data
public class HitResponse {
    private final List<Point> data;
}
