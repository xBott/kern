package me.bottdev.kern.struct.graph;

import me.bottdev.kern.struct.graph.endpoints.Directed;
import me.bottdev.kern.struct.graph.endpoints.Undirected;
import me.bottdev.kern.struct.graph.endpoints.WeightedDirected;
import me.bottdev.kern.struct.graph.endpoints.WeightedUndirected;

/// Factory methods for common endpoint pair implementations.
public final class EndpointPairs {

    /// Creates a directed endpoint pair from `u` to `v`.
    ///
    /// @param u source node
    /// @param v target node
    /// @param <N> node type
    /// @return directed endpoint pair
    public static <N> Directed<N> directed(N u, N v) {
        return new Directed<>(u, v);
    }

    /// Creates an undirected endpoint pair between `u` and `v`.
    ///
    /// @param u first node
    /// @param v second node
    /// @param <N> node type
    /// @return undirected endpoint pair
    public static <N> Undirected<N> undirected(N u, N v) {
        return new Undirected<>(u, v);
    }

    /// Creates a weighted directed endpoint pair from `u` to `v`.
    ///
    /// @param u source node
    /// @param v target node
    /// @param weight edge weight
    /// @param <N> node type
    /// @return weighted directed endpoint pair
    public static <N> WeightedDirected<N> weightedDirected(N u, N v, double weight) {
        return new WeightedDirected<>(u, v, weight);
    }

    /// Creates a weighted undirected endpoint pair between `u` and `v`.
    ///
    /// @param u first node
    /// @param v second node
    /// @param weight edge weight
    /// @param <N> node type
    /// @return weighted undirected endpoint pair
    public static <N> WeightedUndirected<N> weightedUndirected(N u, N v, double weight) {
        return new WeightedUndirected<>(u, v, weight);
    }

}
