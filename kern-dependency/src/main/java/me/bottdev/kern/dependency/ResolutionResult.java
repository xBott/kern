package me.bottdev.kern.dependency;

import org.jspecify.annotations.NonNull;

import java.util.List;

/// The result of a successful dependency resolution. Immutable.
///
/// Provides two views of the sorted dependencies:
/// @param ordered a flat list in topological order (safe sequential execution)
/// @param layers objects grouped into parallel layers; all items within
/// the same layer have no mutual dependencies and can be processed concurrently
///
/// @param <T> type of the resolved objects
public record ResolutionResult<K, T extends DependencyAware<K>>(
       @NonNull List<T> ordered,
       @NonNull List<List<T>> layers
) {

    public static <K, T extends DependencyAware<K>> ResolutionResult<K, T> empty() {
        return new ResolutionResult<>(List.of(), List.of());
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ResolutionResult.Success[\n");
        for (int i = 0; i < layers.size(); i++) {
            sb.append("  layer ").append(i).append(": ").append(layers.get(i)).append('\n');
        }
        sb.append(']');
        return sb.toString();
    }

}
