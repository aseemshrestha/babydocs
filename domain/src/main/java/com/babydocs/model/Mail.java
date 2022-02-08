package com.babydocs.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
public class Mail
{
    @NotNull
    // @Pattern(regexp = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$")
    private String toEmail;
    @NotNull
    private String name;
    @NotNull
    private String subject;
    @NotNull
    private String message;
    @NotNull
    private String randomCode;
    @NotNull
    private String username;

}

