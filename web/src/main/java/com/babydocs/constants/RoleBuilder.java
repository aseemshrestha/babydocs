package com.babydocs.constants;

import com.babydocs.model.Role;
import lombok.Builder;

public class RoleBuilder
{

    private static final Role siteUser;
    private static final Role siteUserR;
    private static final Role admin;
    private static final Role superAdmin;

    static {
        siteUser = Role.builder()
            .id(Roles.SITE_USER_I.getValue())
            .role(Roles.SITE_USER_I.name())
            .build();

        siteUserR = Role.builder()
            .id(Roles.SITE_USER_R.getValue())
            .role(Roles.SITE_USER_R.name())
            .build();

        admin = Role.builder()
            .id(Roles.ADMIN.getValue())
            .role(Roles.ADMIN.name())
            .build();

        superAdmin = Role.builder()
            .id(Roles.SUPER_ADMIN.getValue())
            .role(Roles.SUPER_ADMIN.name())
            .build();

    }

    public static Role getRoleAdmin()
    {
        return admin;
    }

    public static Role getSiteUserR()
    {
        return siteUserR;
    }

    public static Role getSiteUser()
    {
        return siteUser;
    }

    public static Role getSuperAdmin()
    {
        return superAdmin;
    }
}
