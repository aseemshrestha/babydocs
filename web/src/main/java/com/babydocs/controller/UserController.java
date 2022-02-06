package com.babydocs.controller;

import com.babydocs.constants.ApiConstants;
import com.babydocs.constants.RoleBuilder;
import com.babydocs.constants.UserStatus;
import com.babydocs.exceptions.ResourceNotFoundException;
import com.babydocs.logger.AppLogger;
import com.babydocs.model.User;
import com.babydocs.service.AppService;
import com.babydocs.service.UserValidationService;
import eu.bitwalker.useragentutils.UserAgent;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Date;
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
}
