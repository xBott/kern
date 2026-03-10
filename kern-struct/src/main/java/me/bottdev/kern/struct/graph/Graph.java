package me.bottdev.kern.struct.graph;

import me.bottdev.kern.struct.property.PropertyHolder;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public interface Graph<N, E extends EndpointPair<N>> extends PropertyHolder {

    Set<N> nodes();
    Set<E> edges();

    Set<N> adjacentNodes(N node);
    Set<N> successors(N node);
    Set<N> predecessors(N node);

    Set<E> incidentEdges(N node);
    Set<E> outEdges(N node);
    Set<E> inEdges(N node);

    int degree(N node);
    int outDegree(N node);
    int inDegree(N node);

    boolean hasNode(N node);
    boolean hasEdgeConnecting(N nodeU, N nodeV);
    default Set<E> edgesConnecting(N nodeU, N nodeV) {
        return outEdges(nodeU).stream()
                .filter(edge -> edge.hasEndpoint(nodeV))
                .collect(Collectors.toUnmodifiableSet());
    }

    default Optional<E> edgeConnecting(N nodeU, N nodeV) {
        return edgesConnecting(nodeU, nodeV).stream().findFirst();
    }


    default int nodeCount() { return nodes().size(); }
    default int edgeCount() { return edges().size(); }
    default boolean isEmpty() { return nodes().isEmpty(); }

    default boolean allowsParallelEdges() {
        return getProperty(GraphProperties.ALLOWS_PARALLEL_EDGES);
    }

    default boolean allowsSelfLoops() {
        return getProperty(GraphProperties.ALLOWS_SELF_LOOPS);
    }

}
