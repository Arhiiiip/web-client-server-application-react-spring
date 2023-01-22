package com.example.server.security.jwt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Optional;

@Component
@Slf4j
public class JwtTokenFilter extends GenericFilterBean {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        Optional<String> token = jwtTokenProvider.resolveToken(request);
        if (token.isPresent() && jwtTokenProvider.validateToken(token.get())) {
            log.info("Token in exist: {}", token.get());
            Authentication authentication = jwtTokenProvider.getAuthentication(token.get());
            if (authentication != null) SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        log.info("Token isn't exist!");
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
