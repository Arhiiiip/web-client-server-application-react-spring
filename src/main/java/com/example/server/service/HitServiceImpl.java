package com.example.server.service;

import com.example.server.DAO.HitRepo;
import com.example.server.DAO.UserRepo;
import com.example.server.entities.Hit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class HitServiceImpl implements HitService {

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private HitRepo hitRepo;

    @Override
    public void saveHitByUserId(Long userId, Hit hit) {

        userRepo.findById(userId).ifPresent(user -> {
            hit.setUser(user);
            hitRepo.save(hit);
        });

        log.info("Save hit!");
    }

    @Override
    public List<Hit> getAllHitsByUserId(String username) {

        log.info("Returning hits for {}!", username);

        return hitRepo.findAllByUserUsername(username);
    }

    @Override
    public List<Hit> getAllHits() {

        log.info("Returning all hits");

        return hitRepo.findAll();
    }

    @Override
    public void removeAllHitsByUserId(String username) {

        hitRepo.deleteAllByUserUsername(username);

        log.info("Delete hits user {}!", username);
    }

    @Override
    public void removeDotById(String id) {

        hitRepo.deleteById(Long.valueOf(id));

        log.info("Dot {} was deleted!", id);

    }
}
