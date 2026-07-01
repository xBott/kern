package me.bottdev.kern.struct.graph;

import me.bottdev.kern.struct.property.Property;

public interface GraphBuilder<N, E extends EndpointPair<N>> {

    <P> GraphBuilder<N, E> property(Property<P> property, P value);
    GraphBuilder<N, E> allowsSelfLoops(boolean allowsSelfLoops);
    GraphBuilder<N, E> allowsParallelEdges(boolean allowsParallelEdges);

    GraphBuilder<N, E> addNode(N node);

    GraphBuilder<N, E> addEdge(E edge);

    Graph<N, E> immutable();

}
