package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Role {
    ROLE_ADMIN("ROLE_ADMIN"), //관리자
    ROLE_USER("ROLE_USER"), //일반


    ;


    private final String value;

    @JsonCreator
    public static Role from(String s) {
        return Role.valueOf(s.toUpperCase());
    }
}