package com.babydocs.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class ApiException
{
    private Date timestamp;
    private String message;
    private String details;
    private int status;
}
