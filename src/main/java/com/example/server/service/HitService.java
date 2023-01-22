package com.example.server.service;

import com.example.server.entities.Hit;

import java.util.List;

public interface HitService {

    void saveHitByUserId(Long userId, Hit hit);

    List<Hit> getAllHitsByUserId(String username);
    List<Hit> getAllHits();

    void removeAllHitsByUserId(String username);
    void removeDotById(String username);


}
