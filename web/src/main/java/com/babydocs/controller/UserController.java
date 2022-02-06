package com.babydocs.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.babydocs.constants.ApiConstants;
import com.babydocs.constants.RoleBuilder;
import com.babydocs.constants.UserStatus;
import com.babydocs.exceptions.ResourceNotFoundException;
import com.babydocs.logger.AppLogger;
import com.babydocs.model.User;
import com.babydocs.security.JwtProperties;
import com.babydocs.service.AppService;
import com.babydocs.service.UserValidationService;
import eu.bitwalker.useragentutils.UserAgent;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping( ApiConstants.API )
public class UserController
{

    private final AppService appService;
    private final UserValidationService userValidationService;
    private final PasswordEncoder passwordEncoder;

    public UserController(AppService appService, UserValidationService userValidationService,
        PasswordEncoder passwordEncoder)
    {
        this.appService = appService;
        this.userValidationService = userValidationService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping( "v1/secured/get-user/{username}" )
    public ResponseEntity<User> getUser(@PathVariable( "username" ) @NotNull String username,
        HttpServletRequest request) throws Exception
    {
        this.userValidationService.isLoggedUserValid(username, request);
        Optional<User> user = this.appService.getUser(username);
        if (!user.isPresent()) {
            AppLogger.info(UserController.class, "Username " + username + " not found");
            throw new ResourceNotFoundException("Username not found");
        }
        return new ResponseEntity<>(user.get(), HttpStatus.OK);
    }

    @PostMapping( "v1/public/create-user" )
    public ResponseEntity<User> createUser(@RequestBody @Valid User user, HttpServletRequest request)
    {
        UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));

        String ip = request.getHeader("X-FORWARDED-FOR");
        if (ip == null) {
            ip = request.getRemoteAddr();
        }

        User userToSave = User.builder()
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .password(passwordEncoder.encode(user.getPassword()))
            .email(user.getEmail())
            .gender(user.getGender())
            .isActive(UserStatus.ACTIVE.get())
            .ip(ip)
            .username(user.getEmail())
            .browser(
                "%s-%s %s".formatted(userAgent.getBrowser(), userAgent.getBrowserVersion(),
                                     userAgent.getOperatingSystem()))
            .role(RoleBuilder.getSiteUserR())
            .created(new Date())
            .lastUpdated(new Date())
            .build();

        this.appService.saveUser(userToSave);

        AppLogger.info(UserController.class,
                       "User successfully created:" + "name:" + user.getFirstName() + " " + user.getLastName() + " "
                           + user.getEmail());
        return new ResponseEntity<>(userToSave, HttpStatus.CREATED);
    }

    @GetMapping( "v1/public/token-refresh" )
    public ResponseEntity<?> tokenRefresh(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        String tokenHeader = request.getHeader(JwtProperties.REFRESH_TOKEN);
        String _token = tokenHeader.substring("Bearer ".length());
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
