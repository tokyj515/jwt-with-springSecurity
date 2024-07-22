package com.example.demo.service;

import com.example.demo.dto.RoleDto;
import com.example.demo.entity.User;
import com.example.demo.entity.UserDetailsImpl;

import com.example.demo.exception.BadRequestException;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;


    public UserDetails loadUserByUsername(String id) {
        RoleDto roleDto = getUserRoleById(Long.valueOf(id));
        return new UserDetailsImpl(roleDto);
    }

    public RoleDto getUserRoleById(Long id) {
        User user;
        try {
            user = userRepository.findById(id).get();
        }
        catch(NoSuchElementException e) {
            throw new BadRequestException("존재하지 않는 유저입니다.");
        }
        List<String> userRole = new ArrayList<>();
        userRole.add(user.getUserRole());
        return RoleDto.builder()
                .id(id)
                .password(user.getPassword())
                .roles(userRole)
                .build();
    }

}