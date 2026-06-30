package me.bottdev.kern.struct.algorithms.traverse;

import me.bottdev.kern.struct.NeighborProvider;

import java.util.Optional;
import java.util.Set;

public interface TraversalStep<N, T extends NeighborProvider<N>> {

    int depth();
    N node();
    Optional<N> parent();
    Set<N> visited();
    T structure();

    void stop();

}
