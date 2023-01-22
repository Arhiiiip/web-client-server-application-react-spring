package com.example.server.controller;

import com.example.server.DTO.request.AuthRequest;
import com.example.server.DTO.response.AuthResponse;
import com.example.server.converter.UserFormsConverter;
import com.example.server.entities.Roles;
import com.example.server.entities.User;
import com.example.server.security.jwt.JwtTokenProvider;
import com.example.server.security.jwt.JwtUser;
import com.example.server.service.HitService;
import com.example.server.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/api/auth")
public class UserController {

    final UserService userService;
    final HitService hitService;
    final UserFormsConverter formsConverter;
    @Autowired
    private AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public UserController(UserService userService,
                          HitService hitService,
                          UserFormsConverter formsConverter,
                          AuthenticationManager authenticationManager,
                          JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.hitService = hitService;
        this.formsConverter = formsConverter;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody AuthRequest loginRequest, BindingResult bindingResult) {

        try {
            log.info("User {} is entering!", loginRequest);

            if (bindingResult.hasErrors()) {
                log.warn("Login rejected!");
                return ResponseEntity.badRequest().body(null);
            }

            String username = loginRequest.getUsername();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, loginRequest.getPassword()));
            Optional<User> user = userService.findByUsername(username);

            if (!user.isPresent()) {
                log.info("Credentials not found in DB!");
                throw new UsernameNotFoundException("User with username: " + username + " not found");
            }

            log.info("User {} entered successfully!", user.get());

            String token = jwtTokenProvider.createToken(user.get());

            return ResponseEntity.ok(
                    new AuthResponse(username, token, user.get().getRole())
            );
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username or password");
        }

    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody AuthRequest registerRequest, BindingResult bindingResult) {

        log.info("User {} is registering!", registerRequest);

        if (bindingResult.hasErrors()) {
            log.warn("Register rejected!");
            return ResponseEntity.badRequest().body(null);
        }

        User newUser = formsConverter.convertAuthToEntity(registerRequest);

        if (!userService.checkForSavedStateByUsername(newUser.getUsername())) {
            userService.saveUser(newUser);
            log.info("Registered!");

            String token = jwtTokenProvider.createToken(newUser);

            return ResponseEntity.ok(
                    new AuthResponse(newUser.getUsername(), token, Roles.USER)
            );
        } else {
            log.info("Already registered!");
        }

        return ResponseEntity.ok(new AuthResponse());
    }

    @GetMapping("/check")
    public ResponseEntity<?> checkLoginUser() {
        try {

            JwtUser user = resolveJwtUser();
            Optional<User> userJ = userService.findByUsername(user.getUsername());

            return ResponseEntity.ok(
                    new AuthResponse(user.getUsername(), "", userJ.get().getRole())
            );

        }catch (Exception e){
            return ResponseEntity.badRequest().body("Not log");
        }
    }

    private JwtUser resolveJwtUser() {
        Authentication authDetails = SecurityContextHolder.getContext().getAuthentication();
        return (JwtUser) authDetails.getPrincipal();
    }
}
