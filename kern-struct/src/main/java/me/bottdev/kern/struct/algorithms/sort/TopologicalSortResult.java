package me.bottdev.kern.struct.algorithms.sort;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

///The result of a successful topological sort. Immutable.
///
///Provides two views of the sorted objects:
/// - [#ordered()] - a flat list in topological order (safe sequential execution)
/// - [#layers()] - objects grouped into parallel layers; all items within
///the same layer have no mutual dependencies and can be processed concurrently
///
/// @param <T> type of the resolved objects
public final class TopologicalSortResult<T> {

    public static class Builder<T> {

        private final List<List<T>> layers = new ArrayList<>();

        public Builder<T> layer(List<T> layer) {
            layers.add(layer);
            return this;
        }

        public TopologicalSortResult<T> build() {
            List<T> ordered = layers.stream().flatMap(Collection::stream).toList();
            return new TopologicalSortResult<>(ordered, layers);

        }

    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    private final List<T> ordered;
    private final List<List<T>> layers;

    TopologicalSortResult(List<T> ordered, List<List<T>> layers) {
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
