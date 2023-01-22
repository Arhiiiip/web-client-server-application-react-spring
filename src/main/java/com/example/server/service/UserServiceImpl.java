package com.example.server.service;

import com.example.server.DAO.HitRepo;
import com.example.server.DAO.UserRepo;
import com.example.server.entities.Roles;
import com.example.server.entities.Status;
import com.example.server.entities.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private HitRepo hitRepo;

    @Autowired
    private UserRepo userRepo;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public void saveUser(User user) {

        log.info("User save {}!", user);

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Roles.USER);
        user.setStatus(Status.ACTIVE);

        userRepo.save(user);
    }

    @Override
    public boolean checkForSavedStateByUserId(Long userId) {

        return userRepo.existsById(userId);
    }

    @Override
    public List<User> getAllUsers() {
        return (List<User>) userRepo.findAll();
    }

    @Override
    public boolean checkForSavedStateByUsername(String username) {

        return userRepo.existsByUsername(username);
    }

    @Override
    public void removeAllInfoByUserUsername(String username) {

        log.info("Global user delete {}!", username);

        hitRepo.deleteAllByUserUsername(username);
        userRepo.deleteUserByUsername(username);
    }

    @Override
    public void giveRoleByUserUsername(String username, Roles role) {
        log.info("Update role {}!", username);
        if(role == Roles.USER){
            userRepo.updateRoleToUser(username);
        } else if (role == Roles.ADMIN) {
            userRepo.updateRoleToAdmin(username);
        }
    }

    @Override
    public Optional<User> findByUserId(Long userId) {
        return userRepo.findById(userId);
    }

    @Override
    public Optional<User> findByUsername(String username) {

        return userRepo.findByUsername(username);
    }
}
