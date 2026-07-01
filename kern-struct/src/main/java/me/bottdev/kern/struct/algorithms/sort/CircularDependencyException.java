package me.bottdev.kern.struct.algorithms.sort;

import me.bottdev.kern.struct.paths.CyclePath;

public class CircularDependencyException extends Exception {

    private final CyclePath<?> cycleResult;

    public <T> CircularDependencyException(CyclePath<T> cycleResult, String message) {
        super(message);
        this.cycleResult = cycleResult;
    }

    public <T> CircularDependencyException(CyclePath<T> cycleResult, String message, Throwable cause) {
      super(message, cause);
      this.cycleResult = cycleResult;
    }

    @SuppressWarnings("unchecked")
    public <T> CyclePath<T> getCycleResult() {
        return (CyclePath<T>) cycleResult;
    }

}
