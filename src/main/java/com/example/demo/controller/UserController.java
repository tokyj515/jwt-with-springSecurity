package com.example.demo.controller;

import com.example.demo.dto.LoginDto;
import com.example.demo.dto.SignUpDto;
import com.example.demo.dto.UserInfoWithToken;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
@RequestMapping("")
public class UserController {

    private final UserService userService;


    @PostMapping("/signup")
    public Long signup(@RequestBody SignUpDto signUpDto){
        Long userId = userService.signup(signUpDto);
        return userId;
    }


    @PostMapping("/login")
    public UserInfoWithToken login(@RequestBody LoginDto loginDto){
        UserInfoWithToken result = userService.login(loginDto);
        return result;
    }


}
