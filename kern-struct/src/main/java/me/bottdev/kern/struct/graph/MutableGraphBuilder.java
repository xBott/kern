package me.bottdev.kern.struct.graph;

/// Builder extension for mutable graph instances.
///
/// @param <N> node type
/// @param <E> edge endpoint type
public interface MutableGraphBuilder<N, E extends EndpointPair<N>> {

    /// Builds a mutable graph.
    ///
    /// @return mutable graph
    MutableGraph<N, E> mutable();

}
