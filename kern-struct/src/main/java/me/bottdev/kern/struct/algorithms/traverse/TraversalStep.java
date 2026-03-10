package me.bottdev.kern.struct.algorithms.traverse;

import me.bottdev.kern.struct.graph.EndpointPair;
import me.bottdev.kern.struct.graph.Graph;

import java.util.Optional;
import java.util.Set;

public interface TraversalStep<N, E extends EndpointPair<N>> {

    int depth();
    N node();
    Optional<N> parent();
    Optional<E> parentEdge();
    Set<N> visited();
    Graph<N, E> graph();

    void stop();

}
