package com.example.server.controller;

import com.example.server.DTO.request.AuthRequest;
import com.example.server.DTO.request.HitDeleteRequest;
import com.example.server.DTO.response.AuthResponse;
import com.example.server.DTO.response.HitResponse;
import com.example.server.DTO.response.MessageResponse;
import com.example.server.DTO.response.UsersResponse;
import com.example.server.converter.HitFormsConverter;
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
import java.util.stream.Collectors;


@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/api/adm")
public class AdminController {

    final UserService userService;
    final HitService hitService;
    final UserFormsConverter userFormsConverter;
    final HitFormsConverter hitFormsConverter;

    @Autowired
    private AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public AdminController(UserService userService,
                           HitService hitService,
                           UserFormsConverter formsConverter,
                           HitFormsConverter hitFormsConverter, AuthenticationManager authenticationManager,
                           JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.hitService = hitService;
        this.userFormsConverter = formsConverter;
        this.hitFormsConverter = hitFormsConverter;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/delete_user")
    public ResponseEntity<?> removeUser(@RequestBody AuthRequest request, BindingResult bindingResult) {

        try {
            log.info("User {} deleting!", request);

            JwtUser userF = resolveJwtUser();

            if (check(userF.getUsername())) {

                if (bindingResult.hasErrors()) {
                    log.warn("User delete rejected!");
                    return ResponseEntity.badRequest().body(null);
                }

                String username = request.getUsername();
                userService.removeAllInfoByUserUsername(username);

                return ResponseEntity.ok(
                        new MessageResponse("User was deleted!")
                );
            }
            return ResponseEntity.badRequest().body("Not enough rights");
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("User was not deleted!");
        }

    }

    @PostMapping("/delete_dot")
    public ResponseEntity<?> removeDot(@RequestBody HitDeleteRequest request, BindingResult bindingResult) {

        try {
            log.info("Dot {} deleting!", request);

            JwtUser userF = resolveJwtUser();

            if (check(userF.getUsername())) {

                if (bindingResult.hasErrors()) {
                    log.warn("User delete rejected!");
                    return ResponseEntity.badRequest().body(null);
                }

                String Id = request.getId();
                hitService.removeDotById(Id);

                return ResponseEntity.ok(
                        new MessageResponse("Dot was deleted!")
                );
            }
            return ResponseEntity.badRequest().body("Not enough rights");
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Dot was not deleted!l");
        }
    }

    @PostMapping("/give_adm")
    public ResponseEntity<?> giveAdmin(@RequestBody AuthRequest request, BindingResult bindingResult) {

        try {
            log.info("User {} deleting!", request);

            JwtUser userF = resolveJwtUser();

            if (check(userF.getUsername())) {

                if (bindingResult.hasErrors()) {
                    log.warn("User delete rejected!");
                    return ResponseEntity.badRequest().body(null);
                }

                String username = request.getUsername();
                Optional<User> user = userService.findByUsername(username);
                userService.giveRoleByUserUsername(username, Roles.ADMIN);

                return ResponseEntity.ok(
                        new MessageResponse("User is admin!")
                );
            }
            return ResponseEntity.badRequest().body("Not enough rights");
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("User did not admin!");
        }
    }

    @PostMapping("/give_user")
    public ResponseEntity<?> giveRole(@RequestBody AuthRequest request, BindingResult bindingResult) {

        try {
            log.info("User {} deleting!", request);

            JwtUser userF = resolveJwtUser();

            if (check(userF.getUsername())) {

                if (bindingResult.hasErrors()) {
                    log.warn("User delete rejected!");
                    return ResponseEntity.badRequest().body(null);
                }

                String username = request.getUsername();
                Optional<User> user = userService.findByUsername(username);
                userService.giveRoleByUserUsername(username, Roles.USER);

                return ResponseEntity.ok(
                        new MessageResponse("User is admin!")
                );
            }
            return ResponseEntity.badRequest().body("Not enough rights");
            } catch(AuthenticationException e){
                throw new BadCredentialsException("User did not admin!");
            }
        }


    @GetMapping("/get_all_dots")
    public ResponseEntity<?> getAllDots() {

        log.info("Getting all dots!");

        JwtUser user = resolveJwtUser();

        if (check(user.getUsername())) {

            return ResponseEntity.ok().body(
                    new HitResponse(
                            hitService.getAllHits()
                                    .stream()
                                    .map(hitFormsConverter::convertEntityToPoint)
                                    .collect(Collectors.toList()))
            );
        }
        return ResponseEntity.badRequest().body("Not enough rights");
    }



    @GetMapping("/get_all_users")
    public ResponseEntity<?> getAllUsers() {
        log.info("Getting all hits!");

        JwtUser user = resolveJwtUser();

        if (check(user.getUsername())) {

            return ResponseEntity.ok().body(
                    new UsersResponse(
                            userService.getAllUsers()
                                    .stream()
                                    .map(userFormsConverter::convertUserToEntity)
                                    .collect(Collectors.toList()))
            );
        }
        return ResponseEntity.badRequest().body("Not enough rights");
    }

    private boolean check(String username) {
        Optional<User> user = userService.findByUsername(username);
        Roles role = user.get().getRole();
        return role == Roles.OWNER || role == Roles.ADMIN;
    }

    private JwtUser resolveJwtUser() {
        Authentication authDetails = SecurityContextHolder.getContext().getAuthentication();
        return (JwtUser) authDetails.getPrincipal();
    }
}
