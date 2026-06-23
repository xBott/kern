package me.bottdev.kern.struct.graph;

import lombok.NonNull;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;

public interface EndpointPair<N> extends Iterable<N> {

    N nodeU();

    N nodeV();

    boolean isDirected();

    Optional<N> reachableFrom(N node);

    default N adjacentNode(N node) {
        if (node.equals(nodeU())) return nodeV();
        if (node.equals(nodeV())) return nodeU();
        throw new IllegalArgumentException(
                "Node [" + node + "] is not an endpoint of " + this);
    }

    default boolean hasEndpoint(N endpoint) {
        return nodeU().equals(endpoint) || nodeV().equals(endpoint);
    }

    @Override
    @NonNull
    default Iterator<N> iterator() {
        return new Iterator<>() {
            int index = 0;

            @Override
            public boolean hasNext() {
                return index < 2;
            }

            @Override
            public N next() {
                return switch (index++) {
                    case 0 -> nodeU();
                    case 1 -> nodeV();
                    default -> throw new NoSuchElementException();
                };
            }

        };
    }

}
