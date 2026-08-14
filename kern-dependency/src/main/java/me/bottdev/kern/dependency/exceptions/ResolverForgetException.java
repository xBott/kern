package me.bottdev.kern.dependency.exceptions;

import java.util.Set;

public class ResolverForgetException extends RuntimeException {

    private final Set<?> dependents;

    public <K> ResolverForgetException(String message, Set<K> dependents) {
        super(message);
        this.dependents = dependents;
    }

    @SuppressWarnings("unchecked")
    public <K> Set<K> getDependents() {
        return (Set<K>) dependents;
    }

}
