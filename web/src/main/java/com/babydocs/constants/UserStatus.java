package com.babydocs.constants;

public enum UserStatus
{
    ACTIVE(1),
    LOCKED(2),
    DEACTIVATED(3),
    DELETED(4);

    private final int value;

    UserStatus(int value)
    {
        this.value = value;
    }

    public int get()
    {
        return value;
    }
}
