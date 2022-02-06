package com.babydocs.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.CrossOrigin;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

@CrossOrigin
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter
{
    private final AuthenticationManager authenticationManager;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager)
    {
        this.authenticationManager = authenticationManager;
    }

    // on login
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
        throws AuthenticationException
    {
        LoginViewModel credentials = null;
        try {
            credentials = new ObjectMapper().readValue(request.getInputStream(), LoginViewModel.class);
        } catch (IOException ex) {
        }
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
            credentials.getUsername(),
            credentials.getPassword(),
            new ArrayList<>());

        Authentication auth = authenticationManager.authenticate(authenticationToken);
        return auth;

    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
        Authentication authResult)
    {
        CustomUserDetails principal = (CustomUserDetails)authResult.getPrincipal();
        Collection<? extends GrantedAuthority> authorities = principal.getAuthorities();
        String role = "";
        for (GrantedAuthority a : authorities) {
            role = a.getAuthority();
        }
        final String auth_token = JWT.create()
            .withSubject(principal.getUsername())
            .withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.ACCESS_TOKEN_EXPIRATION_TIME))
            .withIssuer(request.getRequestURL().toString())
            .withClaim("role", role)
            .sign(Algorithm.HMAC512(JwtProperties.SECRET.getBytes()));

        final String refresh_token = JWT.create()
            .withSubject(principal.getUsername())
            .withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.REFRESH_TOKEN_EXPIRATION_TIME))
            .withIssuer(request.getRequestURL().toString())
            .sign(Algorithm.HMAC512(JwtProperties.SECRET.getBytes()));

        response.addHeader(JwtProperties.ACCESS_TOKEN, JwtProperties.TOKEN_PREFIX + auth_token);
        response.addHeader(JwtProperties.REFRESH_TOKEN, JwtProperties.TOKEN_PREFIX + refresh_token);
        response.addHeader("FullName", principal.getFullName());

    }
}
