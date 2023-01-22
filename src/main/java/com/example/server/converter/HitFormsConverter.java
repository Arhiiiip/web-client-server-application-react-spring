package com.example.server.converter;

import com.example.server.DTO.request.HitRequest;
import com.example.server.DTO.utility.Coordinates;
import com.example.server.DTO.utility.Point;
import com.example.server.entities.Hit;
import org.springframework.stereotype.Service;

@Service
public class HitFormsConverter {

    public Hit convertHitToEntity(HitRequest hitRequest) {
        Hit hit = new Hit();
        hit.setX(Double.parseDouble(hitRequest.getXValue()));
        hit.setY(Double.parseDouble(hitRequest.getYValue()));
        hit.setR(Double.parseDouble(hitRequest.getRValue()));
        return hit;
    }

    public Point convertEntityToPoint(Hit hitEntity) {
        Point point = new Point();
        point.setId(String.valueOf(hitEntity.getId()));
        point.setHitResult(hitEntity.getResult());
        point.setCoordinates(new Coordinates(hitEntity.getX(), hitEntity.getY(), hitEntity.getR()));
        point.setCurrentTime(hitEntity.getCurrentTime());
        point.setExecutionTime(String.valueOf(hitEntity.getExecutionTime()));
        point.setUser(hitEntity.getUser().getUsername());
        point.setIdUser(String.valueOf(hitEntity.getUser().getId()));
        return point;
    }

}
