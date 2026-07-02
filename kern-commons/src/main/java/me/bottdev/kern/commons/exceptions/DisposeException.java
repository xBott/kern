package me.bottdev.kern.commons.exceptions;

public class DisposeException extends RuntimeException {

    private final Object value;

    public <T> DisposeException(T value, String message) {
        super(message);
        this.value = value;
    }

    public <T> DisposeException(T value, String message, Throwable cause) {
        super(message, cause);
        this.value = value;
    }

    @SuppressWarnings("unchecked")
    public <T> T getValue() {
        return (T) value;
    }
}
