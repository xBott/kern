package me.bottdev.kern.dependency;

import java.util.List;

/// Marker interface for objects that participate in dependency resolution.
///
/// Implement this interface to make your object resolvable. The key uniquely identifies
/// the object in the dependency graph; dependencies are the keys of other objects
/// that must be resolved _before_ or _after_ this one.
///
/// @param <K> type of the key that identifies this object in the graph
public interface DependencyAware<K> {

    /// Returns the unique key that identifies this object in the dependency graph.
    K dependencyKey();

    /// Returns the keys of objects this object directly depends on.
    List<DependencyRequest<K>> getDependencies();

}
