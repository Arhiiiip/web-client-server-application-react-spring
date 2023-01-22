package com.example.server.entities;

import lombok.Getter;


public enum Roles {
    USER("USER"),
    ADMIN("ADMIN"),
    OWNER("OWNER");

    @Getter
    String roleName;

    Roles(String aRoleName) {
        roleName = aRoleName;
    }
}
