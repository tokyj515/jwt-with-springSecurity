package com.example.demo.service;

import com.example.demo.jwt.JwtProvider;
import com.example.demo.dto.LoginDto;
import com.example.demo.dto.SignUpDto;
import com.example.demo.dto.UserInfoWithToken;
import com.example.demo.entity.RefreshToken;
import com.example.demo.entity.User;
import com.example.demo.exception.NotFoundException;
import com.example.demo.repository.RefreshTokenRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.*;

//@DependsOn("env")
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    public Long getUserId(HttpServletRequest httpServletRequest ){
        return Long.valueOf(String.valueOf(httpServletRequest.getAttribute("id")));
    }

    public User getUserByUserId(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if(optionalUser.isEmpty()) {
            throw new NotFoundException("존재하지 않는 회원입니다.");
        }
        return optionalUser.get();
    }

    public Long findUserIdByUsername(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if(optionalUser.isEmpty()) {
            return 0L;
        }
        return optionalUser.get().getId();

    }


    public Long signup(SignUpDto signUpDto){
        signUpDto.setPassword((passwordEncoder.encode(signUpDto.getPassword())));

        User user = User.builder()
                .username(signUpDto.getUsername())
                .password(signUpDto.getPassword())
                .name(signUpDto.getName())
                .build();

        userRepository.save(user);
        Optional<User> optionalUser = userRepository.findByUsername(signUpDto.getUsername());
        if(optionalUser.isEmpty()) {
            throw new NotFoundException();
        }

        //유저 세부내역 등록

        return optionalUser.get().getId();
    }


    public UserInfoWithToken login(LoginDto loginDto) {
        Optional<User> optionalUser = userRepository.findByUsername(loginDto.getUsername());

        if(optionalUser.isPresent()) {
            User user = optionalUser.get();
            if(passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
                String token = jwtProvider.createRefreshToken();
                RefreshToken refreshToken = RefreshToken.builder()
                        .user(user)
                        .token(token)
                        .build();
                refreshTokenRepository.save(refreshToken);


                return UserInfoWithToken.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .token(jwtProvider.createToken(loginDto.getUsername()))
                        .userRole(user.getUserRole())
                        .build();
            }
        }

        return UserInfoWithToken.builder()
                .id(0L)
                .build();
    }


//    public UserRes.TokenReissue tokenReissue(String token) {
//        RefreshToken refreshToken = refreshTokenRepository.findByToken(token);
//        if(refreshToken == null) {
//            throw new NotFoundException("로그인을 다시 진행해주세요.[0]");
//        }
//        User user = refreshToken.getUser();
//        if(user == null) {
//            throw new NotFoundException("로그인을 다시 진행해주세요.[1]");
//        }
//
//        if(!jwtTokenProvider.validateToken(token)) {
//            throw new BadRequestException("로그인을 다시 진행해주세요.[2]");
//        }
//        refreshToken.setDeleteYn(true);
//
//        String accessToken = jwtTokenProvider.createAccessToken(user.getId().toString(), Arrays.asList(user.getUserRole()));
//        String newToken = jwtTokenProvider.createRefreshToken();
//        RefreshToken newRefreshToken = RefreshToken.builder()
//                .user(user)
//                .token(newToken)
//                .build();
//        refreshTokenRepository.save(newRefreshToken);
//
//        return UserRes.TokenReissue.builder()
//                .accessToken(accessToken)
//                .refreshToken(newToken)
//                .build();
//    }
//
//    public void doLogOut(Long userId) {
//        User user = getUserByUserId(userId);
//        List<RefreshToken> refreshTokens = refreshTokenRepository.findByUser(user);
//        for(RefreshToken refreshToken : refreshTokens) {
//            refreshToken.setDeleteYn(true);
//            refreshTokenRepository.save(refreshToken);
//        }
//    }



}
