package me.bottdev.kern.struct.graph.endpoints;

import lombok.Setter;

/// Undirected endpoint pair with a mutable weight.
///
/// @param <N> node type
public final class WeightedUndirected<N> extends Undirected<N> implements Weighted {

    @Setter
    private double weight;

    /// Creates a weighted undirected endpoint pair.
    ///
    /// @param source first node
    /// @param target second node
    /// @param weight edge weight
    public WeightedUndirected(N source, N target, double weight) {
        super(source, target);
        this.weight = weight;
    }

    @Override
    public double weight() {
        return weight;
    }

}
