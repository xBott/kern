package me.bottdev.kern.struct.algorithms.traverse;

import me.bottdev.kern.struct.NeighborProvider;
import me.bottdev.kern.struct.graph.EndpointPair;
import me.bottdev.kern.struct.graph.Graph;

import java.util.Iterator;

@FunctionalInterface
public interface TraversalOrder {

    <N, T extends NeighborProvider<N>> TraversalIterator<N, T> createIterator(
            T structure,
            N start,
            boolean allowDuplicates
    );

}