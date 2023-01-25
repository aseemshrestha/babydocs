package com.babydocs.constants;

public class AppConstants {


    public static final String APP_NAME = "Babydocs";
    public static final String TEAM = "Team";

    public interface Welcome {
        String WELCOME_PASS_SUBJECT = "Welcome to Babydocs";
        String WELCOME_MESSAGE =
                "Thank you for signing up.\n "
                        + "Super excited to have you on board, we know you’ll just love us.\n ";
    }

    public interface ForgotPassword {
        String subject = "Password Reset Request";
        String message = "You recently requested for password reset. We are here to help.\n "
                + "Please use the code below to reset your password.\n ";
        Integer expiryHrs = 24;
        String validMins = "Please note above code is valid for only 24 hours.";
    }


}
