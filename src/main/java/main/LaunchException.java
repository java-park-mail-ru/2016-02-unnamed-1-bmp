package main;


public class LaunchException extends Exception {
    public LaunchException(String error, Exception e) {
        super(error, e);
    }
}