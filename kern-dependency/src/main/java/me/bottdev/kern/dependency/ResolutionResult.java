package me.bottdev.kern.dependency;

import me.bottdev.kern.dependency.exceptions.AggregatedDependencyException;
import me.bottdev.kern.dependency.exceptions.DependencyException;

import java.util.List;

///The result of a dependency resolution. Immutable.
///
/// @param <T> type of the resolved objects
public sealed interface ResolutionResult<K, T extends DependencyAware<K>> permits
        ResolutionResult.Success,
        ResolutionResult.Failure
{

    boolean isSuccess();

    default Success<K, T> orElseThrow() throws DependencyException {
        if (this instanceof Success<K, T> success) return success;
        Failure<K, T> failure = (Failure<K, T>) this;
        throw new AggregatedDependencyException(failure.diagnostics());
    }

    /// The result of a successful dependency resolution. Immutable.
    ///
    /// Provides two views of the sorted dependencies:
    /// @param ordered a flat list in topological order (safe sequential execution)
    /// @param layers objects grouped into parallel layers; all items within
    /// the same layer have no mutual dependencies and can be processed concurrently
    ///
    /// @param <T> type of the resolved objects
    record Success<K, T extends DependencyAware<K>>(
            List<T> ordered,
            List<List<T>> layers
    ) implements ResolutionResult<K, T> {

        public Success(List<T> ordered, List<List<T>> layers) {
            this.ordered = List.copyOf(ordered);
            this.layers = layers.stream().map(List::copyOf).toList();
        }

        @Override
        public boolean isSuccess() {
            return true;
        }

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

    /// The result of a successful dependency resolution. Immutable.
    ///
    /// @param diagnostics a list of all found problems while resolution of dependencies.
    ///
    /// @param <T> type of the resolved objects
    record Failure<K, T extends DependencyAware<K>>(
            List<DependencyDiagnostic<K>> diagnostics
    ) implements ResolutionResult<K, T> {

        public Failure(List<DependencyDiagnostic<K>> diagnostics) {
            this.diagnostics = List.copyOf(diagnostics);
        }

        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        public String toString() {
            return "ResolutionResult.Failure[diagnostics=" + diagnostics + ")";
        }
    }

}
