package com.babydocs.controller;

import com.babydocs.constants.ApiConstants;
import com.babydocs.constants.AppConstants;
import com.babydocs.constants.RoleBuilder;
import com.babydocs.constants.UserStatus;
import com.babydocs.email.EmailService;
import com.babydocs.email.MailType;
import com.babydocs.exceptions.BadRequestException;
import com.babydocs.exceptions.GenericAppException;
import com.babydocs.exceptions.ResourceNotFoundException;
import com.babydocs.logger.AppLogger;
import com.babydocs.model.*;
import com.babydocs.service.UserAndRoleService;
import com.babydocs.service.PasswordResetService;
import com.babydocs.service.UserValidationService;
import com.babydocs.utils.AppUtils;
import com.babydocs.utils.ResetCodeGenerator;
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
import java.util.concurrent.TimeUnit;

import static com.babydocs.constants.AppConstants.RESET_PASS_EXPIRY_HOURS;

@RestController
@RequestMapping( ApiConstants.API )
public record UserController(UserAndRoleService userAndRoleService, UserValidationService userValidationService,
                             PasswordEncoder passwordEncoder, EmailService emailService,
                             PasswordResetService passwordResetService)
{
    @GetMapping( "v1/secured/get-user/{username}" )
    public ResponseEntity<User> getUser(@PathVariable( "username" ) @NotNull String username,
        HttpServletRequest request) throws Exception
    {
        this.userValidationService.isLoggedUserValid(username, request);
        Optional<User> user = this.userAndRoleService.getUser(username);
        if (user.isEmpty()) {
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

        final User savedUser = this.userAndRoleService.saveUser(userToSave);

        AppLogger.info(UserController.class,
                       "User successfully created:" + "name:" + user.getFirstName() + " " + user.getLastName() + " "
                           + user.getEmail());
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @PostMapping( "v1/public/password-reset" )
    public ResponseEntity<?> forgotPassword(@RequestBody @NotNull ForgotPassword passReset)
    {

        Optional<User> user = this.userAndRoleService.getUser(passReset.getEmail());
        if (user.isEmpty()) {
            throw new ResourceNotFoundException("User not found. Please try again");
        }
        String sixDigitNumber = ResetCodeGenerator.generateResetCode();
        Mail mail =
            Mail.builder().toEmail(passReset.getEmail()).subject(AppConstants.FORGOT_PASS_SUBJECT)
                .name(user.get().getFirstName())
                .username(user.get().getUsername())
                .randomCode(sixDigitNumber)
                .message(AppConstants.FORGOT_PASS_MESSAGE).build();

        PasswordReset pr = new PasswordReset();
        pr.setUsername(user.get().getUsername());
        pr.setResetCode(sixDigitNumber);
        pr.setExpiresAt(AppUtils.addHours(new Date(), RESET_PASS_EXPIRY_HOURS));
        pr.setCreated(new Date());
        pr.setLastUpdated(new Date());

        try {
            this.emailService.sendMail(mail, MailType.PASSWORD_RESET);
            this.passwordResetService.savePasswordReset(pr);
        } catch (Exception ex) {
            throw new GenericAppException("Error in sending mail");
        }
        return new ResponseEntity<>("Resent link has been successfully sent to your email address.", HttpStatus.OK);
    }

    @PostMapping( "v1/public/password-reset-submit" )
    public ResponseEntity<?> passwordResetSubmit(@RequestBody @Valid PasswordResetSubmit passwordResetSubmit)
    {
        if (!passwordResetSubmit.getPassword().equals(passwordResetSubmit.getConfirmPassword())) {
            throw new BadRequestException("Password and Confirm password do not match.");
        }
        Optional<PasswordReset> passwordReset =
            passwordResetService.getPasswordReset(passwordResetSubmit.getResetCode());
        if (passwordReset.isEmpty()) {
            throw new BadRequestException("Reset Code not found. Please try again");
        }
        if (!passwordResetSubmit.getUsername().equals(passwordReset.get().getUsername())) {
            throw new BadRequestException("Username doesn't match. Please try again");
        }
        Optional<User> user = userAndRoleService.getUser(passwordResetSubmit.getUsername());
        if (user.isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }
        if (passwordEncoder.matches(passwordResetSubmit.getPassword(), user.get().getPassword())) {
            throw new BadRequestException("You cannot use the same password you have used before.");
        }
        long duration = new Date().getTime() - passwordReset.get().getExpiresAt().getTime();
        long hoursDiff = TimeUnit.MILLISECONDS.toHours(duration);
        if (hoursDiff > RESET_PASS_EXPIRY_HOURS) {
            throw new ResourceNotFoundException(
                "Reset Code is expired. Please resubmit the password reset request.");
        }
        this.userAndRoleService.updatePassword(passwordReset.get().getUsername(),
                                               passwordEncoder.encode(passwordResetSubmit.getPassword()));

        return new ResponseEntity<>("Password Successfully updated. Please relogin with new password.", HttpStatus.OK);

    }

}
