package me.bottdev.kern.meta.core.exceptions;

public class MetaException extends RuntimeException {

    public MetaException(String message) {
        super(message);
    }

    public  MetaException(String message, Throwable cause) {
        super(message, cause);
    }

}
