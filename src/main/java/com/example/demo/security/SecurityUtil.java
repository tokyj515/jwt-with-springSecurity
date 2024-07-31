package com.example.demo.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public class SecurityUtil {

    private static final Logger logger = LoggerFactory.getLogger(SecurityUtil.class);

    private SecurityUtil() {
    }


    public static Optional<String> getCurrentUsername() {

//        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        UserDetails userDetails = (UserDetails)principal;
//
//        String username = userDetails.getUsername();
//        String password = userDetails.getPassword();
//        기본적으로 Spring Security의 principal 객체는 Object 형태로 UserDetails를 형변환 해야 한다.

        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            logger.debug("Security Context에 인증 정보가 없습니다.");
            return Optional.empty();
        }

        String username = null;
        if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails springSecurityUser = (UserDetails) authentication.getPrincipal();
            username = springSecurityUser.getUsername();
        } else if (authentication.getPrincipal() instanceof String) {
            username = (String) authentication.getPrincipal();
        }

        return Optional.ofNullable(username);
    }

    public static void printThreadInfo() {
        // 현재 스레드 정보 출력
        Thread currentThread = Thread.currentThread();
        System.out.println("Thread Name: " + currentThread.getName());
        System.out.println("Thread ID: " + currentThread.getId());
        System.out.println("Thread State: " + currentThread.getState());
        System.out.println("Thread Priority: " + currentThread.getPriority());

        // SecurityContextHolder에서 인증 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            System.out.println("Authenticated: " + authentication.isAuthenticated());
            System.out.println("Principal: " + authentication.getPrincipal());
            System.out.println("Authorities: " + authentication.getAuthorities());
        } else {
            System.out.println("No authentication information available");
        }
    }



}