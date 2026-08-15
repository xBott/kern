package me.bottdev.kern.dependency.versioned;

import lombok.NonNull;
import me.bottdev.kern.dependency.DependOrder;
import me.bottdev.kern.dependency.DependencyLink;
import me.bottdev.kern.dependency.DependencyRequest;
import me.bottdev.kern.version.VersionRange;

/// Implementation of [DependencyRequest] used to define a versioned dependency of [VersionedDependencyAware] class.
///
/// @param key key that identifies the dependency object
/// @param link indicates how strongly the dependencies are linked to one another
/// @param order position of object regarding the dependency
/// @param versionRange the required version range of the dependency
///
/// @param <K> type of the key that identifies the dependency object in the graph
public record VersionedDependencyRequest<K>(
        @NonNull K key,
        @NonNull DependencyLink link,
        @NonNull DependOrder order,
        @NonNull VersionRange versionRange
) implements DependencyRequest<K> {}
