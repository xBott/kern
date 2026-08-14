package me.bottdev.kern.dependency;

import me.bottdev.kern.dependency.exceptions.ResolverForgetException;

import java.util.Collection;
import java.util.Set;

/// Represents a state of a dependency resolver.
/// Used to store information about already resolved dependencies.
/// Allows to perform incremental dependency resolution.
public interface DependencyResolverState<K, T extends DependencyAware<K>> {

    /// @return Indicates whether the resolver has already resolved specified dependency.
    boolean remembers(K key);

    /// @return Collection of committed dependencies.
    Collection<T> committed();

    /// @return Dependent associated with a provided key or null.
    T get(K key);

    /// @return A set of keys that depend on a specified dependency.
    Set<K> dependentsOf(K key);

    /// Saves a dependent to the state.
    void commit(T dependent);

    /// Removes dependent with a provided key from context of resolver.
    /// @throws ResolverForgetException when some objects depend on a specified dependency.
    void forget(K key) throws ResolverForgetException;

}
