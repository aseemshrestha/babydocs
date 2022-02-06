package com.babydocs.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public final class AppLogger
{
    public static void info(Class<?> clazz, String logMessage)
    {
        Logger LOG = LoggerFactory.getLogger(clazz);
        if (LOG.isInfoEnabled()) {
            LOG.info(logMessage);
        }
    }

    public static void debug(Class<?> clazz, String logMessage)
    {
        Logger LOG = LoggerFactory.getLogger(clazz);
        if (LOG.isDebugEnabled()) {
            LOG.debug(logMessage);
        }
    }
}
