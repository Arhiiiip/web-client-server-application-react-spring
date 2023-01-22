package com.example.server.service;

import com.example.server.entities.Hit;
import org.springframework.stereotype.Service;

@Service
public class AreaHitChecker implements AreaCheck {

    @Override
    public boolean checkHitResult(Hit aHit) {
        return isSquareZone(aHit) || isСircleZone(aHit) || isTrigleZone(aHit);
    }

    private boolean isСircleZone(Hit aHit) {
        double x = aHit.getX();
        double y = aHit.getY();
        double r = aHit.getR();

        return (x <= 0) && (y <= 0) && x*x + y*y <= r*r;
    }

    private boolean isSquareZone(Hit aHit) {
        double x = aHit.getX();
        double y = aHit.getY();
        double r = aHit.getR();

        return (x >= 0) && (y >= 0) && (x <= r/2) && (y <= r);
    }

    private boolean isTrigleZone(Hit aHit) {
        double x = aHit.getX();
        double y = aHit.getY();
        double r = aHit.getR();

        return (x >= 0) && (y <= 0) && (y >= 2*x - r);
    }
}
