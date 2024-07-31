package com.example.demo.jwt;


import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;

import java.io.IOException;

import static java.util.Objects.isNull;

public class JwtAuthenticationFilter extends GenericFilterBean {


    private final JwtProvider jwtProvider;

    public JwtAuthenticationFilter(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        String token = jwtProvider.resolveToken((HttpServletRequest) servletRequest);

        if(!isNull(token) && jwtProvider.validateToken(token)) {
            Authentication authentication = jwtProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            servletRequest.setAttribute("id", Long.valueOf(jwtProvider.getUserId(token)));
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
