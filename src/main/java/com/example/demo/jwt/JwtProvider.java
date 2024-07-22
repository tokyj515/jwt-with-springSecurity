package com.example.demo.jwt;



import com.example.demo.entity.Token;
import io.jsonwebtoken.*;

import javax.annotation.PostConstruct;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;


import java.util.Base64;
import java.util.Date;

@RequiredArgsConstructor
@Component
public class JwtProvider {

    private String accessSecretKey = "test";

    private String refreshSecretKey = "test";

    //유효시간 7일
    private long accessTokenValidTime = 7 * 24 * 60 * 60 * 1000L;
    //유효시간 31일
    private long refreshTokenValidTime = 30 * 24 * 60 * 60 * 1000L;

    private final UserDetailsService userDetailsService;





    // 객체 초기화, secretKey를 Base64로 인코딩
    @PostConstruct
    protected void init() {
        accessSecretKey = Base64.getEncoder().encodeToString(accessSecretKey.getBytes());
        refreshSecretKey = Base64.getEncoder().encodeToString(refreshSecretKey.getBytes());
    }

    // 토큰 생성
    public Token createToken(String userPk) {  // userPK = email
        Claims claims = Jwts.claims().setSubject(userPk); // JWT payload 에 저장되는 정보단위
        Date now = new Date();
        String accessToken = getToken(claims, now, accessTokenValidTime, accessSecretKey);
        String refreshToken = getToken(claims, now, refreshTokenValidTime, refreshSecretKey);
        return Token.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .key(userPk)
                .build();
    }

    public String createRefreshToken() {
        Date now = new Date();

        return Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshTokenValidTime))
                .signWith(SignatureAlgorithm.HS256, refreshSecretKey)
                .compact();
    }

    private String getToken(Claims claims, Date currentTime, long tokenValidTime, String secretKey) {
        return Jwts.builder()
                .setClaims(claims) //정보 저장
                .setIssuedAt(currentTime)  //토큰 발행시간 정보
                .setExpiration(new Date(currentTime.getTime() + tokenValidTime)) //Expire Time
                .signWith(SignatureAlgorithm.HS256, secretKey)  //암호화 알고리즘
                .compact();
    }

    // 인증 정보 조회
    public Authentication getAuthentication(String token) {
        validationAuthorizationHeader(token);
        String available_token = extractToken(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUserPk(available_token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // 토큰에서 회원 정보 추출
    public String getUserPk(String token) {
        return Jwts.parser()
                .setSigningKey(accessSecretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Request의 Header에서 token 값 가져오기
//    public String getAccessToken(HttpServletRequest request) {
//        return request.getHeader("Authorization");
//    }

    //토큰만 추출하기
    private String extractToken(String authorizationHeader) {
        return authorizationHeader.substring("Bearer ".length());
    }

    private void validationAuthorizationHeader(String header) {
        if (header == null || !header.startsWith("Bearer ")) {
            throw new IllegalArgumentException();
        }
    }

    //토큰의 유효성 검사
    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(accessSecretKey).parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        }
        catch (Exception e) {
            return false;
        }
    }
//    public boolean validateToken(ServletRequest request, String jwtToken) {
//        try {
//            validationAuthorizationHeader(jwtToken);
//            String token = extractToken(jwtToken);
//            userDetailsService.loadUserByUsername(this.getUserPk(token));
//            Jws<Claims> claims = Jwts.parser().setSigningKey(accessSecretKey).parseClaimsJws(token);
//            return !claims.getBody().getExpiration().before(new Date());
//        } catch (SignatureException e) {
//            e.printStackTrace();
//            request.setAttribute("exception", "ForbiddenException");
//        } catch (MalformedJwtException e) {
//            e.printStackTrace();
//            request.setAttribute("exception", "MalformedJwtException");
//        } catch (ExpiredJwtException e) {
//            //토큰 만료시
//            e.printStackTrace();
//            request.setAttribute("exception", "ExpiredJwtException");
//        } catch (UnsupportedJwtException e) {
//            e.printStackTrace();
//            request.setAttribute("exception", "UnsupportedJwtException");
//        } catch (IllegalArgumentException e) {
//            e.printStackTrace();
//            request.setAttribute("exception", "IllegalArgumentException");
//        }
//        return false;
//    }

    // RefreshToken 유효성 검증 메소드
//    public String validateRefreshToken(RefreshToken refreshTokenObj) {
//        String refreshToken = refreshTokenObj.getRefreshToken();
//
//        try {
//            Jws<Claims> claims = Jwts.parser().setSigningKey(refreshSecretKey).parseClaimsJws(refreshToken);
//            //AccessToken이 만료되지않았을떄만
//            /*if(!claims.getBody().getExpiration().before(new Date())) {
//                return recreationAccessToken(claims.getBody().get("sub").toString(), claims.getBody().get("roles"));
//            }*/
//            return recreateAccessToken(claims.getBody().get("sub").toString(), claims.getBody().get("roles"));
//        }catch (Exception e) {
//            e.printStackTrace();
//            throw new AuthenticationCustomException(ErrorCode.EXPIRED_JWT);
//        }
//        //토큰 만료시 login페이지 reDirect
//    }

    //AccessToken 새로 발급
    private String recreateAccessToken(String email, Object roles) {
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("roles", roles);
        Date currentTime = new Date();
        return getToken(claims, currentTime, accessTokenValidTime, accessSecretKey);
    }

    public String resolveToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7); // "Bearer " 접두사 이후의 토큰 부분을 반환
        }
        return null; // 헤더가 없거나 "Bearer "로 시작하지 않으면 null 반환
    }



    public String getUsername(String token) {
        String username;
        try {
            username = Jwts.parser().setSigningKey(accessSecretKey).parseClaimsJws(token).getBody().getSubject();
        }
        catch(ExpiredJwtException e) {
            username = e.getClaims().getSubject();
            return username;
        }
        return username;
    }
}