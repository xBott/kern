package me.bottdev.kern.struct.graph;

public interface MutableGraph<N, E extends EndpointPair<N>> extends Graph<N, E> {

    boolean addNode(N node);

    boolean removeNode(N node);

    boolean addEdge(E edge);

    boolean removeEdge(E edge);

}
