package main;

public class LaunchException extends Exception {
    @Override
    public String getMessage() {
        return "Unable to start server";
    }
}
