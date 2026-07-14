package me.bottdev.kern.struct;

/// An interface for data-structures that are used for path algorithms
/// Composes 3 interfaces: [ElementProvider] [NeighborProvider] [WeightProvider]
public interface PathStructure<N> extends ElementProvider<N>, NeighborProvider<N>, WeightProvider<N> {



}
