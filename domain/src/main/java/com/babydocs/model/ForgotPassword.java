package com.babydocs.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
public class ForgotPassword {
    @NotNull
    private String email;
}
