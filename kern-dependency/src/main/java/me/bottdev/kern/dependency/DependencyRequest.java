package me.bottdev.kern.dependency;

/// Interface used to define a dependency of [DependencyAware] class.
/// @param <K>  type of the key that identifies the dependency object in the graph
public interface DependencyRequest<K> {

    /// returns key that identifies the dependency object
    K key();

    /// indicates how strongly the dependencies are linked to one another
    DependencyLink link();

    /// return position of object regarding the dependency
    DependOrder order();


}
