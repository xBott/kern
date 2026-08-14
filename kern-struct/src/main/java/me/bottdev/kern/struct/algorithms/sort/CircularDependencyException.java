package me.bottdev.kern.struct.algorithms.sort;

import me.bottdev.kern.struct.paths.CyclePath;

public class CircularDependencyException extends Exception {

    private final CyclePath<?> cyclePath;

    public <T> CircularDependencyException(CyclePath<T> cyclePath, String message) {
        super(message);
        this.cyclePath = cyclePath;
    }

    public <T> CircularDependencyException(CyclePath<T> cyclePath, String message, Throwable cause) {
      super(message, cause);
      this.cyclePath = cyclePath;
    }

    @SuppressWarnings("unchecked")
    public <T> CyclePath<T> getCyclePath() {
        return (CyclePath<T>) cyclePath;
    }

}
