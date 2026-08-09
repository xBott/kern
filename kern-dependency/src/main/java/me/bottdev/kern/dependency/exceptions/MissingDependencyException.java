package me.bottdev.kern.dependency.exceptions;

public class MissingDependencyException extends DependencyException {

    private final Object dependent;
    private final Object dependencyKey;

    public <T, K> MissingDependencyException(T dependent, K dependencyKey, String message) {
        super(message);
        this.dependent = dependent;
        this.dependencyKey = dependencyKey;
    }

    @SuppressWarnings("unchecked")
    public <T> T getDependent() {
        return (T) dependent;
    }

    @SuppressWarnings("unchecked")
    public <K> K getDependencyKey() {
        return (K) dependencyKey;
    }

}
