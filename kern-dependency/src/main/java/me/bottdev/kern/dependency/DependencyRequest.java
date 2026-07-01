package me.bottdev.kern.dependency;

/// Class used to define a dependency of [DependencyAware] class.
///
/// @param key key that identifies the dependency object
/// @param link indicates how strongly the dependencies are linked to one another
/// @param order position of object regarding the dependency
///
/// @param <K> type of the key that identifies the dependency object in the graph
public record DependencyRequest<K>(
        K key,
        DependencyLink link,
        DependOrder order
) {}
