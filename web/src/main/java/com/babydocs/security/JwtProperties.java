package com.babydocs.security;

public class JwtProperties
{
    public static final String SECRET = "kathmandu";
    public static final int ACCESS_TOKEN_EXPIRATION_TIME = 60000000; // 1 min
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String ACCESS_TOKEN = "Authorization";
    public static final String REFRESH_TOKEN = "Refresh";
    public static final Long REFRESH_TOKEN_EXPIRATION_TIME = 3155760000000L;

}
