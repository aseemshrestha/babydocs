package com.babydocs.exceptions;

import java.util.Date;

public record ApiException(Date timestamp, String message, String details, int status)
{
}
