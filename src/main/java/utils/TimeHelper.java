package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TimeHelper {
    private static final Logger LOGGER = LogManager.getLogger(TimeHelper.class);
    public static void sleep(int stepTime) {
        try {
            Thread.sleep(stepTime);
        } catch (InterruptedException e) {
            LOGGER.error("Thread interrupted", e);
        }
    }

}