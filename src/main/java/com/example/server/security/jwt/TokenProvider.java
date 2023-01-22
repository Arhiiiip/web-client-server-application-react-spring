package com.example.server.security.jwt;

import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

public interface TokenProvider<T> {

    String createToken(T obj);

    /**
     * Check authentication status of user by token's data
     *
     * @return Result's authentication token
     */
    Authentication getAuthentication(String token);

    /**
     * Unpacking token body from servletRequest
     */
    Optional<String> resolveToken(HttpServletRequest req);

    boolean validateToken(String token);
}
