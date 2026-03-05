package me.bottdev.kern.struct.graph.traverse;

import me.bottdev.kern.struct.graph.EndpointPair;
import me.bottdev.kern.struct.graph.Graph;

import java.util.Iterator;

@FunctionalInterface
public interface TraversalOrder<N, E extends EndpointPair<N>> {
    Iterator<TraversalStep<N, E>> createIterator(Graph<N, E> graph, N start);
}