package me.bottdev.kern.struct.algorithms.traverse;

import me.bottdev.kern.struct.NeighborProvider;

import java.util.Iterator;

public interface TraversalIterator<N, T extends NeighborProvider<N>> extends Iterator<TraversalStep<N, T>> {



}
