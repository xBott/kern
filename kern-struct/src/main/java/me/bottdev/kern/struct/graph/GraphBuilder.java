package me.bottdev.kern.struct.graph;

import me.bottdev.kern.struct.property.Property;

/// Builder for immutable graphs.
///
/// @param <N> node type
/// @param <E> edge endpoint type
public interface GraphBuilder<N, E extends EndpointPair<N>> {

    /// Sets a graph property.
    ///
    /// @param property property key
    /// @param value property value
    /// @param <P> property value type
    /// @return this builder
    <P> GraphBuilder<N, E> property(Property<P> property, P value);

    /// Enables or disables self-loop edges.
    ///
    /// @param allowsSelfLoops true to allow self-loops
    /// @return this builder
    GraphBuilder<N, E> allowsSelfLoops(boolean allowsSelfLoops);

    /// Enables or disables parallel edges.
    ///
    /// @param allowsParallelEdges true to allow parallel edges
    /// @return this builder
    GraphBuilder<N, E> allowsParallelEdges(boolean allowsParallelEdges);

    /// Adds a node to the graph being built.
    ///
    /// @param node node to add
    /// @return this builder
    GraphBuilder<N, E> addNode(N node);

    /// Adds an edge to the graph being built.
    ///
    /// @param edge edge to add
    /// @return this builder
    GraphBuilder<N, E> addEdge(E edge);

    /// Builds an immutable graph.
    ///
    /// @return immutable graph
    Graph<N, E> immutable();

}
