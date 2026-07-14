package me.bottdev.kern.struct.graph;

import lombok.NonNull;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;

/// Pair of endpoint nodes that represents an edge.
///
/// @param <N> node type
public interface EndpointPair<N> extends Iterable<N> {

    /// Returns the first endpoint, or the source for directed edges.
    ///
    /// @return first endpoint
    N nodeU();

    /// Returns the second endpoint, or the target for directed edges.
    ///
    /// @return second endpoint
    N nodeV();

    /// Returns true for directed edges.
    ///
    /// @return true if this pair is directed
    boolean isDirected();

    /// Returns the node reachable from `node`, if any.
    ///
    /// @param node start node
    /// @return reachable endpoint
    Optional<N> reachableFrom(N node);

    /// Returns the opposite endpoint of `node`.
    ///
    /// @param node one endpoint
    /// @return opposite endpoint
    default N adjacentNode(N node) {
        if (node.equals(nodeU())) return nodeV();
        if (node.equals(nodeV())) return nodeU();
        throw new IllegalArgumentException(
                "Node [" + node + "] is not an endpoint of " + this);
    }

    /// Returns true if `endpoint` is one of this edge's endpoints.
    ///
    /// @param endpoint endpoint to check
    /// @return true if the endpoint belongs to this pair
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
