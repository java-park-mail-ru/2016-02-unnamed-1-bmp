package dbservice;

public class DatabaseException extends Exception {

    public DatabaseException(String error, RuntimeException e) {
        super(error, e);
    }

}