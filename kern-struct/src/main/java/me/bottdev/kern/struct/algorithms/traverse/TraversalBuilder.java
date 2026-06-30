package me.bottdev.kern.struct.algorithms.traverse;
import lombok.RequiredArgsConstructor;
import me.bottdev.kern.struct.NeighborProvider;

import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@RequiredArgsConstructor
public class TraversalBuilder<N, T extends NeighborProvider<N>> {

    private final TraversalOrder order;
    private final T structure;

    private N start;
    private boolean allowDuplicates = false;

    public TraversalBuilder<N, T> from(N start) {
        this.start = start;
        return this;
    }

    public TraversalBuilder<N, T> allowDuplicates(boolean allowDuplicates) {
        this.allowDuplicates = allowDuplicates;
        return this;
    }

    private void validate() {
        Objects.requireNonNull(structure, "Traversable structure must be non-null.");
        Objects.requireNonNull(start, "Start node must be non-null.");
        Objects.requireNonNull(order, "Traversal order must be non-null.");
    }

    public TraversalIterator<N, T> iterator() {
        validate();
        return order.createIterator(structure, start, allowDuplicates);
    }

    public Stream<TraversalStep<N, T>> stream() {
        TraversalIterator<N, T> iterator = iterator();
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED),
                false
        );
    }

}
