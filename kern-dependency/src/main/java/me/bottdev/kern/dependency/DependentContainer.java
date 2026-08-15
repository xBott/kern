package me.bottdev.kern.dependency;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/// Container that stores a list of [DependencyAware] objects.
/// Used by resolver as an argument.
public interface DependentContainer<K, T extends DependencyAware<K>> {

    boolean isEmpty();

    /// @return a set of [K] keys.
    Set<K> keys();

    /// @return a list of [DependencyAware] objects.
    Collection<T> values();

    /// @return Indicates whether the container has an object with a specified key.
    boolean contains(K key);

    /// @return Object associated with a provided key or null.
    T get(K key);

    /// @return Map with objects associated with their keys.
    Map<K, T> toMap();

}
