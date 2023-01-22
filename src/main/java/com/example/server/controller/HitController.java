package com.example.server.controller;

import com.example.server.DTO.request.HitRequest;
import com.example.server.DTO.response.HitResponse;
import com.example.server.converter.HitFormsConverter;
import com.example.server.entities.Hit;
import com.example.server.security.jwt.JwtUser;
import com.example.server.service.AreaCheck;
import com.example.server.service.HitService;
import com.example.server.service.UserService;
import com.example.server.service.validators.ValidateR;
import com.example.server.service.validators.ValidateXY;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.stream.Collectors;

@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/hit")
public class HitController {

    final UserService userService;
    final HitService hitService;
    final HitFormsConverter hitFormsConverter;
    final AreaCheck areaHitChecker;

    public HitController(UserService userService,
                         HitService hitService,
                         HitFormsConverter hitFormsConverter,
                         AreaCheck areaHitChecker) {
        this.userService = userService;
        this.hitService = hitService;
        this.hitFormsConverter = hitFormsConverter;
        this.areaHitChecker = areaHitChecker;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addHit(@RequestBody HitRequest addHitRequest, BindingResult bindingResult) {

        long serviceStartTime = System.nanoTime();
        log.info("Client sent point coordinates: {}!", addHitRequest);


        if (bindingResult.hasErrors()) {
            log.warn("Point add rejected!");
            return ResponseEntity.badRequest().build();
        }

        if (ValidateXY.validate(addHitRequest.getXValue()) &&
                ValidateXY.validate(addHitRequest.getYValue()) &&
                ValidateR.validate(addHitRequest.getRValue())
        ) {
            Hit newHit = hitFormsConverter.convertHitToEntity(addHitRequest);

            newHit.setResult(areaHitChecker.checkHitResult(newHit));
            newHit.setCurrentTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            newHit.setExecutionTime((double) (System.nanoTime() - serviceStartTime) / 100000);

            JwtUser user = resolveJwtUser();
            log.info("User id: {}", user.getId());
            hitService.saveHitByUserId(user.getId(), newHit);

            return ResponseEntity.ok().body(
                    new HitResponse(
                            Collections.singletonList(
                                    hitFormsConverter.convertEntityToPoint(newHit)
                            )
                    )
            );
        } else {
            return ResponseEntity.badRequest().body("Request have trouble");
        }
    }

    @GetMapping("/remove_all")
    public ResponseEntity<?> removeAllHits() {

        log.info("Removing all hits!");

        JwtUser user = resolveJwtUser();
        hitService.removeAllHitsByUserId(user.getUsername());

        return ResponseEntity.ok().body(
                new HitResponse(
                        Collections.EMPTY_LIST
                )
        );
    }

    @GetMapping("/get_all")
    public ResponseEntity<?> getAllHits() {

        log.info("Getting all hits!");

        JwtUser user = resolveJwtUser();

        return ResponseEntity.ok().body(
                new HitResponse(
                        hitService.getAllHitsByUserId(user.getUsername())
                                .stream()
                                .map(hitFormsConverter::convertEntityToPoint)
                                .collect(Collectors.toList()))
        );
    }

    private JwtUser resolveJwtUser() {
        Authentication authDetails = SecurityContextHolder.getContext().getAuthentication();
        return (JwtUser) authDetails.getPrincipal();
    }
}
