package me.bottdev.kern.struct.graph;

import me.bottdev.kern.struct.graph.adjacency.AdjacencyListGraphBuilder;
import me.bottdev.kern.struct.property.Property;

public interface GraphBuilder<N, E extends EndpointPair<N>> {

    <P> void property(Property<P> property, P value);
    void allowsSelfLoops(boolean allowsSelfLoops);
    void allowsParallelEdges(boolean allowsParallelEdges);

    GraphBuilder<N, E> addNode(N node);

    GraphBuilder<N, E> addEdge(E edge);

    Graph<N, E> immutable();

    MutableGraph<N, E> mutable();

}
