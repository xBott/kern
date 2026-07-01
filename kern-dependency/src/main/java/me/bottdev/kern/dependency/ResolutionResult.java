package me.bottdev.kern.dependency;

import me.bottdev.kern.struct.algorithms.sort.TopologicalSortResult;

import java.util.ArrayList;
import java.util.List;

///The result of a successful dependency resolution. Immutable.
///
///Provides two views of the sorted dependencies:
/// - [#ordered()] - a flat list in topological order (safe sequential execution)
/// - [#layers()] - objects grouped into parallel layers; all items within
///the same layer have no mutual dependencies and can be processed concurrently
///
/// @param <T> type of the resolved objects
public final class ResolutionResult<T> {

    private final List<T> ordered;
    private final List<List<T>> layers;

    public ResolutionResult(List<T> ordered, List<List<T>> layers) {
        this.ordered = List.copyOf(ordered);
        this.layers  = layers.stream().map(List::copyOf).toList();
    }

    /// Flat topological order - each element appears after all its dependencies.
    public List<T> ordered() {
        return ordered;
    }

    /// Parallel layers. Items within the same layer are independent of each other
    /// and can be initialized / executed concurrently.
    /// Layers themselves must be processed in list order.
    public List<List<T>> layers() {
        return layers;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ResolutionResult{\n");
        for (int i = 0; i < layers.size(); i++) {
            sb.append("  layer ").append(i).append(": ").append(layers.get(i)).append('\n');
        }
        sb.append('}');
        return sb.toString();
    }

}
