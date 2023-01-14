package com.babydocs.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ForgotPassword
{
    private String email;
}
