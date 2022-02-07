package com.babydocs.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.babydocs.constants.ApiConstants;
import com.babydocs.exceptions.BadRequestException;
import com.babydocs.logger.AppLogger;
import com.babydocs.model.User;
import com.babydocs.security.JwtProperties;
import com.babydocs.service.AppService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping( ApiConstants.API )
public record TokenController(AppService appService)
{
    @GetMapping( "v1/public/token-refresh" )
    public ResponseEntity<?> tokenRefresh(HttpServletRequest request)
    {
        String tokenHeader = request.getHeader(JwtProperties.REFRESH_TOKEN);
        if (tokenHeader == null) {
            throw new BadRequestException("Refresh token is missing");
        }
        String _token = tokenHeader.substring(JwtProperties.TOKEN_PREFIX.length());
        Algorithm algorithm = Algorithm.HMAC512(JwtProperties.SECRET.getBytes());
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = verifier.verify(_token);
        String username = decodedJWT.getSubject();
        Optional<User> user = appService.getUser(username);
        String role = "";
        if (user.isPresent()) {
            User _user = user.get();
            role = _user.getRole().getRole();
        }

        final String auth_token = JWT.create()
            .withSubject(username)
            .withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.ACCESS_TOKEN_EXPIRATION_TIME))
            .withIssuer(request.getRequestURL().toString())
            .withClaim("role", "ROLE_" + role)
            .sign(Algorithm.HMAC512(JwtProperties.SECRET.getBytes()));

        AppLogger.info(UserController.class, "Auth Token created: " + auth_token);
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("access_token", JwtProperties.TOKEN_PREFIX + auth_token);
        tokenMap.put("refresh_token", tokenHeader);
        return new ResponseEntity<>(tokenMap, HttpStatus.OK);

    }
}
