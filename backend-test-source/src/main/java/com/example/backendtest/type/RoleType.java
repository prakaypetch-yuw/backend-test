package com.example.backendtest.type;

import lombok.Getter;

@Getter
public enum RoleType {
    USER("USER");
    private final String name;

    RoleType(String name) {
        this.name = name;
    }
}
