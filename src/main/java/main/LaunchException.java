package main;

import org.hibernate.HibernateException;

public class LaunchException extends Exception {
    private HibernateException hibernateException;
    String launchError;

    public LaunchException(String error, HibernateException e) {
        this.launchError = error;
        this.hibernateException = e;
    }

    public String getError() {
        return this.launchError + ' ' + hibernateException.getMessage();
    }

    public HibernateException getHibernateException() {
        return hibernateException;
    }

    public void setHibernateException(HibernateException hibernateException) {
        this.hibernateException = hibernateException;
    }
}
