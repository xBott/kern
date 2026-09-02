package me.bottdev.kern.dependency.simple;

import lombok.NonNull;
import me.bottdev.kern.dependency.DependOrder;
import me.bottdev.kern.dependency.DependencyLink;
import me.bottdev.kern.dependency.DependencyRequest;

/// Simple implementation of [DependencyRequest]
///
/// @param key key that identifies the dependency object
/// @param link indicates how strongly the dependencies are linked to one another
/// @param order position of object regarding the dependency
///
/// @param <K> type of the key that identifies the dependency object in the graph
public record SimpleDependencyRequest<K>(
        @NonNull K key,
        @NonNull DependencyLink link,
        @NonNull DependOrder order
) implements DependencyRequest<K> {}
