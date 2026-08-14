package me.bottdev.kern.dependency.containers;

import me.bottdev.kern.dependency.DependencyAware;
import me.bottdev.kern.dependency.DependentContainer;

import java.util.*;

/// Simple implementation of [DependentContainer]. Immutable.
/// Use [SimpleDependentContainer#builder()] method to create a container with a builder.
public record SimpleDependentContainer<K, T extends DependencyAware<K>>(
        Map<K, T> dependentsMap
) implements DependentContainer<K, T> {

    public static class Builder<K, T extends DependencyAware<K>> {

        private final Map<K, T> dependentsMap = new HashMap<>();

        public Builder<K, T> add(T dependent) {
            dependentsMap.put(dependent.dependencyKey(), dependent);
            return this;
        }

        public Builder<K, T> add(List<T> dependents) {
            dependents.forEach(this::add);
            return this;
        }

        public SimpleDependentContainer<K, T> build() {
            return new SimpleDependentContainer<>(dependentsMap);
        }

    }

    public static <K, T extends DependencyAware<K>> Builder<K, T> builder() {
        return new Builder<>();
    }

    @Override
    public boolean isEmpty() {
        return dependentsMap.isEmpty();
    }

    @Override
    public Set<K> keys() {
        return Collections.unmodifiableSet(dependentsMap.keySet());
    }

    @Override
    public Collection<T> values() {
        return Collections.unmodifiableCollection(dependentsMap.values());
    }

    @Override
    public boolean contains(K key) {
        return dependentsMap.containsKey(key);
    }

    @Override
    public T get(K key) {
        return dependentsMap.get(key);
    }

    @Override
    public Map<K, T> toMap() {
        return Collections.unmodifiableMap(dependentsMap);
    }
}
