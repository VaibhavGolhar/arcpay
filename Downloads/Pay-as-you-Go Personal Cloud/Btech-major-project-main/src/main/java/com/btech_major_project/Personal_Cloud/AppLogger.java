package com.btech_major_project.Personal_Cloud;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppLogger {

    private final Logger logger;

    private AppLogger(Class<?> clazz) {
        this.logger = LoggerFactory.getLogger(clazz);
    }

    public static AppLogger getLogger(Class<?> clazz) {
        return new AppLogger(clazz);
    }

    public void info(String message) {
        logger.info(message);
    }

    public void warn(String message) {
        logger.warn(message);
    }

    public void error(String message, Throwable t) {
        logger.error(message, t);
    }

    public void debug(String message) {
        logger.debug(message);
    }
}