package me.bottdev.kern.struct.graph.endpoints;

import lombok.Setter;

/// Directed endpoint pair with a mutable weight.
///
/// @param <N> node type
public final class WeightedDirected<N> extends Directed<N> implements Weighted {

    @Setter
    private double weight;

    /// Creates a weighted directed endpoint pair.
    ///
    /// @param source source node
    /// @param target target node
    /// @param weight edge weight
    public WeightedDirected(N source, N target, double weight) {
        super(source, target);
        this.weight = weight;
    }

    @Override
    public double weight() {
        return weight;
    }

}
