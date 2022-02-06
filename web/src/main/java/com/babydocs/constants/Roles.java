package com.babydocs.constants;

public enum Roles
{
    SITE_USER_I(1),
    SITE_USER_R(2),
    ADMIN(3),
    SUPER_ADMIN(4);

    private final int value;

    Roles(int value)
    {
        this.value = value;
    }

    public int getValue()
    {
        return value;
    }
}
