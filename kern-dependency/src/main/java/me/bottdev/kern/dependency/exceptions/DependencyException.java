package me.bottdev.kern.dependency.exceptions;

public class DependencyException extends Exception {

    public DependencyException(String message) {
        super(message);
    }

    public DependencyException(String message, Throwable cause) {
        super(message, cause);
    }

}
