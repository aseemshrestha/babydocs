package com.babydocs.utils;

import java.security.SecureRandom;
import java.util.Random;

public class ResetCodeGenerator
{
    public static String generateResetCode()
    {
        Random rnd = new SecureRandom();
        int number = rnd.nextInt(999999);
        return String.format("%06d", number);
    }
}
