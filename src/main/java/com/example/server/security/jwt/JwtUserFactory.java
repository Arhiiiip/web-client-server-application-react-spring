package com.example.server.security.jwt;

import com.example.server.entities.Roles;
import com.example.server.entities.Status;
import com.example.server.entities.User;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
public class JwtUserFactory {

    public static JwtUser create(User user) {

        return new JwtUser(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                mapToGrantedAuthorities(Collections.singletonList(user.getRole())),
                isEnabled(user.getStatus())
        );
    }

    private static boolean isEnabled(Status userStatus) {
        return userStatus == Status.ACTIVE;
    }

    private static List<GrantedAuthority> mapToGrantedAuthorities(List<Roles> userRoles) {
        return userRoles
                .stream()
                .map(role ->
                        new SimpleGrantedAuthority(role.getRoleName()))
                .collect(Collectors.toList());
    }
}
