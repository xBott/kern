package me.bottdev.kern.dependency;

import lombok.NonNull;

/// Interface used to define a dependency of [DependencyAware] class.
/// @param <K>  type of the key that identifies the dependency object in the graph
public interface DependencyRequest<K> {

    /// returns key that identifies the dependency object
    @NonNull
    K key();

    /// indicates how strongly the dependencies are linked to one another
    @NonNull DependencyLink link();

    /// return position of object regarding the dependency
    @NonNull DependOrder order();


}
