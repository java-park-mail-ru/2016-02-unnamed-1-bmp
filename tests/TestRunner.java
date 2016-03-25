import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class TestRunner {
    private static final Logger LOGGER = LogManager.getLogger();

    public static void main(String[] args) {
        final Result result = JUnitCore.runClasses(JunitTestSuite.class);
        for (Failure failure : result.getFailures()) {
            LOGGER.info(failure.toString());
        }
        LOGGER.info(result.wasSuccessful());
    }
}  