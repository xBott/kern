package me.bottdev.kern.struct.algorithms.traverse;
import lombok.RequiredArgsConstructor;
import me.bottdev.kern.struct.NeighborProvider;

import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@RequiredArgsConstructor
public class TraversalStructureSelector {

    private final TraversalOrder order;

    public <N, T extends NeighborProvider<N>> TraversalBuilder<N, T> on(T structure) {
        return new TraversalBuilder<>(order, structure);
    }

}
