package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Role {
    ROLE_ADMIN("ROLE_ADMIN"), //회장
    ROLE_ASEM("ROLE_ASEM"), //부회장
    ROLE_BSEM("ROLE_BSEM"), //총무
    ROLE_CSEM("ROLE_CSEM"), //서기
    ROLE_DSEM("ROLE_DSEM"), //홍보
    ROLE_ESEM("ROLE_ESEM"), //스터디
    ROLE_USER("ROLE_USER"); //일반 유저


    private String value;

    @JsonCreator
    public static Role from(String s) {
        return Role.valueOf(s.toUpperCase());
    }
}