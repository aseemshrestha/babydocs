package com.babydocs.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class PasswordResetSubmit
{
    @NotNull
    private String username;
    @NotEmpty( message = "Reset Code is required." )
    private String resetCode;
    @NotEmpty( message = "Password is required." )
    private String password;
    @NotEmpty( message = "Confirm Password is required." )
    private String confirmPassword;
}
