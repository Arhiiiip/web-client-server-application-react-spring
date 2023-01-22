package com.example.server.service;

import com.example.server.entities.Hit;
import com.example.server.entities.Roles;
import com.example.server.entities.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    void saveUser(User user);

    boolean checkForSavedStateByUserId(Long userId);
    List<User> getAllUsers();

    boolean checkForSavedStateByUsername(String username);
    void removeAllInfoByUserUsername(String username);
    void giveRoleByUserUsername(String username, Roles role);

    Optional<User> findByUserId(Long userId);

    Optional<User> findByUsername(String username);
}
