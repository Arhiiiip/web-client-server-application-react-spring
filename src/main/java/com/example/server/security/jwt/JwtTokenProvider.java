package com.example.server.security.jwt;

import com.example.server.entities.Roles;
import com.example.server.entities.User;
import com.example.server.security.JwtUserDetailsService;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
public class JwtTokenProvider implements TokenProvider<User> {
    @Autowired
    private final UserDetailsService userDetailsService;
    @Value("${jwt.token.secretSequence}")
    private String secretSequence;
    @Value("${jwt.token.expired}")
    private long tokenTimeToLiveMS;

    public JwtTokenProvider(JwtUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @PostConstruct
    protected void init() {
        secretSequence = Base64.getEncoder().encodeToString(secretSequence.getBytes());
    }


    @Override
    public String createToken(User obj) {
        Claims claims = Jwts.claims().setSubject(obj.getUsername());
        claims.put("roles", getRoleNames(Collections.singletonList(obj.getRole())));

        Date now = new Date();
        Date tokenTimeToLiveDate = new Date(now.getTime() + tokenTimeToLiveMS);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(tokenTimeToLiveDate)
                .signWith(SignatureAlgorithm.HS256, secretSequence)
                .compact();
    }

    @Override
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(getUsername(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    @Override
    public Optional<String> resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer_")) {
            return Optional.of(bearerToken.substring(7));
        }
        return Optional.empty();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretSequence).parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtAuthenticationException("JWT token is expired or invalid");
        }
    }

    private String getUsername(String token) {
        return Jwts.parser().setSigningKey(secretSequence).parseClaimsJws(token).getBody().getSubject();
    }

    private List<String> getRoleNames(List<Roles> userRoles) {

        return userRoles
                .stream()
                .map(Roles::getRoleName)
                .collect(Collectors.toList());
    }
}
