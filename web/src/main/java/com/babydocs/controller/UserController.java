package com.babydocs.controller;

import com.babydocs.constants.AppConstants;
import com.babydocs.constants.RoleBuilder;
import com.babydocs.constants.UserStatus;
import com.babydocs.email.EmailService;
import com.babydocs.email.MailType;
import com.babydocs.exceptions.BadRequestException;
import com.babydocs.exceptions.GenericAppException;
import com.babydocs.exceptions.ResourceNotFoundException;
import com.babydocs.logger.AppLogger;
import com.babydocs.model.Activity;
import com.babydocs.model.ChangePasswordDTO;
import com.babydocs.model.Mail;
import com.babydocs.model.PasswordReset;
import com.babydocs.model.PasswordResetSubmit;
import com.babydocs.model.User;
import com.babydocs.service.ActivityService;
import com.babydocs.service.PasswordResetService;
import com.babydocs.service.UserAndRoleService;
import com.babydocs.service.ValidationService;
import com.babydocs.utils.AppUtils;
import com.babydocs.utils.ResetCodeGenerator;
import eu.bitwalker.useragentutils.UserAgent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("api/")
@Slf4j
public record UserController(UserAndRoleService userAndRoleService, ValidationService validationService,
                             PasswordEncoder passwordEncoder, EmailService emailService,
                             PasswordResetService passwordResetService, ActivityService activityService) {
    @GetMapping("v1/secured/get-user/{username}")
    public ResponseEntity<User> getUser(@PathVariable("username") @NotNull String username,
                                        HttpServletRequest request) throws Exception {
        this.validationService.isLoggedUserValid(username, request);
        Optional<User> user = this.userAndRoleService.getUser(username);
        if (user.isEmpty()) {
            AppLogger.info(UserController.class, "Username " + username + " not found");
            throw new ResourceNotFoundException("Username not found");
        }
        return new ResponseEntity<>(user.get(), HttpStatus.OK);
    }

    @PostMapping("v1/public/create-user")
    public ResponseEntity<User> createUser(@RequestBody @Valid User user, HttpServletRequest request) throws InterruptedException {
        UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));

        String ip = request.getHeader("X-FORWARDED-FOR");
        if (ip == null) {
            ip = request.getRemoteAddr();
        }

        user.setPassword(this.passwordEncoder.encode(user.getPassword()));
        user.setIp(ip);
        user.setLastUpdated(new Date());
        user.setUsername(user.getEmail());
        user.setBrowser(userAgent.getBrowser().getName() + "-" + userAgent.getOperatingSystem());
        user.setRole(RoleBuilder.getSiteUserR());
        user.setCreated(new Date());
        user.setIsActive(UserStatus.ACTIVE.get());

        final User savedUser = this.userAndRoleService.saveUser(user);

        log.info("User successfully created:" + "name:" + user.getFirstName() + " " + user.getLastName() + " " + user.getEmail());
        Mail mail =
                Mail.builder().toEmail(user.getEmail()).subject(AppConstants.Welcome.WELCOME_PASS_SUBJECT)
                        .name(user.getFirstName())
                        .username(user.getUsername())
                        .message(AppConstants.Welcome.WELCOME_MESSAGE).build();
        System.out.println("Current Thread:" + Thread.currentThread().getName());
        emailService.sendMail(mail, MailType.WELCOME);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }


    @PostMapping("v1/public/password-reset")
    public ResponseEntity<?> forgotPassword(@RequestParam(value = "email") String email) {

        Optional<User> user = this.userAndRoleService.getUser(email);
        if (user.isEmpty()) {
            throw new ResourceNotFoundException("User not found. Please try again");
        }
        String sixDigitNumber = ResetCodeGenerator.generateResetCode();
        Mail mail =
                Mail.builder().toEmail(email).subject(AppConstants.ForgotPassword.subject)
                        .name(user.get().getFirstName())
                        .username(user.get().getUsername())
                        .randomCode(sixDigitNumber)
                        .message(AppConstants.ForgotPassword.message).build();


        PasswordReset pr = new PasswordReset();
        pr.setUsername(user.get().getUsername());
        pr.setResetCode(sixDigitNumber);
        pr.setExpiresAt(AppUtils.addHours(new Date(), AppConstants.ForgotPassword.expiryHrs));
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

    @PostMapping("v1/public/password-reset-submit")
    public ResponseEntity<?> passwordResetSubmit(@RequestBody @Valid PasswordResetSubmit passwordResetSubmit) {
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
        if (hoursDiff > AppConstants.ForgotPassword.expiryHrs) {
            throw new ResourceNotFoundException(
                    "Reset Code is expired. Please resubmit the password reset request.");
        }
        this.userAndRoleService.updatePassword(passwordReset.get().getUsername(),
                passwordEncoder.encode(passwordResetSubmit.getPassword()));
        var activity = new Activity();
        activity.setEventOwner(user.get().getFirstName());
        activity.setEventOwner(user.get().getFirstName() + " with " + user.get().getUsername() + " password update completed");
        activity.setEventDate(new Date());
        activityService.saveActivity(activity);

        return new ResponseEntity<>("Password Successfully updated. Please relogin with new password.", HttpStatus.OK);

    }

    @PatchMapping("v1/secured/password-change")
    public ResponseEntity<?> changePassword(@RequestBody @Valid ChangePasswordDTO changePasswordDTO) {
        if (!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getConfirmNewPassword())) {
            throw new BadRequestException("Password and Confirm password do not match");
        }
        Optional<User> user = this.userAndRoleService.getUser(changePasswordDTO.getUsername());
        if (user.isEmpty()) {
            throw new ResourceNotFoundException("User not found. Please try again.");
        }
        boolean matches = this.passwordEncoder.matches(changePasswordDTO.getCurrentPassword(), user.get().getPassword());
        if (!matches) {
            throw new BadRequestException("Current password do not match.");
        }
        user.get().setPassword(this.passwordEncoder.encode(changePasswordDTO.getNewPassword()));
        userAndRoleService.saveUser(user.get());
        return new ResponseEntity<>("Password has been successfully changed.", HttpStatus.OK);
    }
}
