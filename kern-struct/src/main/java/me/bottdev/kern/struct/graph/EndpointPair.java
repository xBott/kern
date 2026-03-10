package me.bottdev.kern.struct.graph;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;

public abstract class EndpointPair<N> implements Iterable<N> {

    public static <N> Directed<N> directed(N source, N target) {
        return new Directed<>(source, target);
    }

    public static <N> WeightedDirected<N> directed(N source, N target, double weight) {
        return new WeightedDirected<>(source, target, weight);
    }

    public static <N> Undirected<N> undirected(N nodeU, N nodeV) {
        return new Undirected<>(nodeU, nodeV);
    }

    public static <N> WeightedUndirected<N> undirected(N nodeU, N nodeV, double weight) {
        return new WeightedUndirected<>(nodeU, nodeV, weight);
    }

    public abstract N nodeU();

    public abstract N nodeV();

    public abstract boolean isDirected();

    public abstract Optional<N> reachableFrom(N node);

    public N adjacentNode(N node) {
        if (node.equals(nodeU())) return nodeV();
        if (node.equals(nodeV())) return nodeU();
        throw new IllegalArgumentException(
                "Node [" + node + "] is not an endpoint of " + this);
    }

    public boolean hasEndpoint(N endpoint) {
        return nodeU().equals(endpoint) || nodeV().equals(endpoint);
    }

    @Override
    @NonNull
    public Iterator<N> iterator() {
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

    @RequiredArgsConstructor
    public static class Directed<N> extends EndpointPair<N> {

        private final N source;
        private final N target;

        public N source() {
            return source;
        }

        public N target() {
            return target;
        }

        @Override
        public N nodeU() {
            return source;
        }

        @Override
        public N nodeV() {
            return target;
        }

        @Override
        public boolean isDirected() {
            return true;
        }

        @Override
        public Optional<N> reachableFrom(N node) {
            if (node.equals(source)) return Optional.of(target);
            if (node.equals(target)) return Optional.empty();
            throw new IllegalArgumentException(
                    "Node [" + node + "] is not an endpoint of " + this
            );
        }

        @Override
        public boolean equals(Object other) {
            if  (this == other) return true;
            if (!(other instanceof Directed<?> directed)) return false;
            return source.equals(directed.source) && target.equals(directed.target);
        }

        @Override
        public int hashCode() {
            return 31 * source.hashCode() + target.hashCode();
        }

        @Override
        public String toString() { return source + " -> " + target; }

    }

    @RequiredArgsConstructor
    public static class Undirected<N> extends EndpointPair<N> {

        private final N nodeU;
        private final N nodeV;

        @Override
        public N nodeU() {
            return nodeU;
        }

        @Override
        public N nodeV() {
            return nodeV;
        }

        @Override
        public boolean isDirected() {
            return false;
        }

        @Override
        public Optional<N> reachableFrom(N node) {
            if (node.equals(nodeU)) return Optional.of(nodeV);
            if (node.equals(nodeV)) return Optional.of(nodeU);
            throw new IllegalArgumentException(
                    "Node [" + node + "] is not an endpoint of " + this
            );
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Undirected<?> u)) return false;
            return (nodeU.equals(u.nodeU) && nodeV.equals(u.nodeV))
                || (nodeU.equals(u.nodeV) && nodeV.equals(u.nodeU));
        }

        @Override
        public int hashCode() {
            return nodeU.hashCode() + nodeV.hashCode();
        }

        @Override
        public String toString() { return nodeU + " -- " + nodeV; }

    }

    public static class WeightedDirected<N>
            extends EndpointPair.Directed<N>
            implements Weighted {

        private final double weight;

        public WeightedDirected(N source, N target, double weight) {
            super(source, target);
            this.weight = weight;
        }

        @Override
        public double weight() {
            return weight;
        }

        @Override
        public String toString() { return source() + " -[" + weight + "]-> " + target(); }
    }

    public static class WeightedUndirected<N>
            extends EndpointPair.Undirected<N>
            implements Weighted {

        private final double weight;

        public WeightedUndirected(N nodeU, N nodeV, double weight) {
            super(nodeU, nodeV);
            this.weight = weight;
        }

        @Override
        public double weight() {
            return weight;
        }

        @Override
        public String toString() { return nodeU() + " -[" + weight + "]- " + nodeV(); }
    }

}
