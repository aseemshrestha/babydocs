package com.babydocs.seed;

import com.babydocs.constants.RoleBuilder;
import com.babydocs.model.Role;
import com.babydocs.service.AppService;
import com.babydocs.utils.ConfigUtility;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DbInit implements CommandLineRunner
{
    private final ConfigUtility configUtility;
    private final AppService appService;

    public DbInit(ConfigUtility configUtility, AppService appService)
    {
        this.configUtility = configUtility;
        this.appService = appService;
    }

    @Override
    public void run(String... args) throws Exception
    {
        List<Role> roles = new ArrayList<>();

        roles.add(RoleBuilder.getRoleAdmin());
        roles.add(RoleBuilder.getSiteUserR());
        roles.add(RoleBuilder.getSiteUser());
        roles.add(RoleBuilder.getSuperAdmin());

        roles.forEach(r -> appService.saveRole(r));

    }

}
