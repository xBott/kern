package me.bottdev.kern.dependency.versioned;

import me.bottdev.kern.dependency.DependencyAware;
import me.bottdev.kern.dependency.DependencyRequest;
import me.bottdev.kern.version.SemVersion;
import java.util.List;

/// Marker interface for versioned objects that participate in dependency resolution.
///
/// @param <K> type of the key that identifies this object in the graph
public interface VersionedDependencyAware<K> extends DependencyAware<K> {

    /// Returns the version of this object.
    SemVersion getVersion();

    /// Returns the versioned dependency requests of this object.
    List<VersionedDependencyRequest<K>> getVersionedDependencies();

    /// Default implementation that translates versioned dependency requests
    /// to standard dependency requests for compatibility.
    @Override
    default List<DependencyRequest<K>> getDependencies() {
        return getVersionedDependencies().stream()
                .map(versioned -> (DependencyRequest<K>) versioned)
                .toList();
    }

}
