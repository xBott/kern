package me.bottdev.kern.struct.graph.endpoints;

import lombok.RequiredArgsConstructor;
import me.bottdev.kern.struct.graph.EndpointPair;

import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
public class Directed<N> implements EndpointPair<N> {

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
        if (!(other instanceof Directed<?> that)) return false;
        return Objects.equals(this.source, that.source()) &&
                Objects.equals(this.target, that.target());
    }

    @Override
    public int hashCode() {
        return 31 * source().hashCode() + target.hashCode();
    }

    @Override
    public String toString() { return source + " -> " + target; }

}

