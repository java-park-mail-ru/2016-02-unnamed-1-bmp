package dbservice;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DatabaseException extends Exception {
    private static final Logger LOGGER = LogManager.getLogger(DatabaseException.class);
    private RuntimeException runtimeException;

    public DatabaseException( RuntimeException e ) {
        this.runtimeException = e;
    }

    @Override
    public String getMessage() {
        return "Fail to perform db transaction";
    }

    public DatabaseException(String message) {
        super(message);
        LOGGER.error(message);
    }

    public RuntimeException getRuntimeException() {
        return runtimeException;
    }

    public void setRuntimeException(RuntimeException runtimeException) {
        this.runtimeException = runtimeException;
    }
}
