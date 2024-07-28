package com.example.demo.controller;

import com.example.demo.dto.LoginDto;
import com.example.demo.dto.SignUpDto;
import com.example.demo.dto.UserInfoWithToken;
import com.example.demo.entity.ApiResponse;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("")
public class UserController {

    private final UserService userService;


    @GetMapping("/test")
    public String test(){
        return "test";
    }


    @PostMapping("/signup")
    public ApiResponse<Long> signup(@RequestBody SignUpDto signUpDto){
        return new ApiResponse<>(userService.signup(signUpDto));
    }


    @PostMapping("/login")
    public ApiResponse<UserInfoWithToken> login(@RequestBody LoginDto loginDto){
        return new ApiResponse<>(userService.login(loginDto));
    }


}
