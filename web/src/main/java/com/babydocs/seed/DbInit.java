package com.babydocs.seed;

import com.babydocs.constants.RoleBuilder;
import com.babydocs.model.Role;
import com.babydocs.service.UserAndRoleService;
import com.babydocs.utils.ConfigUtility;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DbInit implements CommandLineRunner
{
    private final UserAndRoleService userAndRoleService;

    public DbInit(ConfigUtility configUtility, UserAndRoleService userAndRoleService)
    {
        this.userAndRoleService = userAndRoleService;
    }

    @Override
    public void run(String... args) {
        List<Role> roles = new ArrayList<>();

        roles.add(RoleBuilder.getRoleAdmin());
        roles.add(RoleBuilder.getSiteUserR());
        roles.add(RoleBuilder.getSiteUser());
        roles.add(RoleBuilder.getSuperAdmin());

        roles.forEach(r -> userAndRoleService.saveRole(r));

    }

}
