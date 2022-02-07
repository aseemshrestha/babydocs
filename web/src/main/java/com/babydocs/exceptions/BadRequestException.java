package com.babydocs.exceptions;

public final class BadRequestException extends RuntimeException
{
    public BadRequestException(String message)
    {
        super(message);
    }
}
