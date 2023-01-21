package com.babydocs;

public class Constants {

    public enum PostType {
        PRIVATE, WITHIN_NETWORK, PUBLIC
    }

    public enum Gender {
        MALE, FEMALE
    }

    public enum ActivityType {
        LOGIN,
        LOGOUT,
        LIKE,
        SWITCH_POST_TYPE,
        POST,
        COMMENT,
        CHANGE_PASS,
        FOLLOW
    }
}
