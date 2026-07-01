package me.bottdev.kern.dependency.containers;

import me.bottdev.kern.dependency.DependencyAware;
import me.bottdev.kern.dependency.DependentContainer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/// Simple implementation of [DependentContainer]. Immutable.
/// Use [SimpleDependentContainer#builder()] method to create a container with a builder.
public record SimpleDependentContainer<K, T extends DependencyAware<K>>(
        List<T> dependents
) implements DependentContainer<K, T> {

    public static class Builder<K, T extends DependencyAware<K>> {

        private final List<T> dependents = new ArrayList<>();

        public Builder<K, T> add(T dependent) {
            dependents.add(dependent);
            return this;
        }

        public Builder<K, T> add(List<T> dependents) {
            this.dependents.addAll(dependents);
            return this;
        }

        public SimpleDependentContainer<K, T> build() {
            return new SimpleDependentContainer<>(dependents);
        }

    }

    public static <K, T extends DependencyAware<K>> Builder<K, T> builder() {
        return new Builder<>();
    }

    @Override
    public List<T> dependents() {
        return Collections.unmodifiableList(dependents);
    }

}
