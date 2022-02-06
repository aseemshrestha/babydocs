package com.babydocs.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class ConfigUtility
{

    @Autowired
    public Environment environment;

    public String getProperty(String propertyKey)
    {
        return environment.getProperty(propertyKey);
    }

    public String[] getActiveProfiles()
    {
        return environment.getActiveProfiles();
    }
}

