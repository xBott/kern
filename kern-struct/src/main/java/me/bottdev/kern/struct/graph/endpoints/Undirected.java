package me.bottdev.kern.struct.graph.endpoints;

import lombok.RequiredArgsConstructor;
import me.bottdev.kern.struct.graph.EndpointPair;

import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
public class Undirected<N> implements EndpointPair<N> {

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
        if (!(o instanceof Undirected<?> that)) return false;
        return ( Objects.equals(this.nodeU, that.nodeU()) && Objects.equals(this.nodeV, that.nodeV()) ) ||
                ( Objects.equals(this.nodeV, that.nodeU()) && Objects.equals(this.nodeU, that.nodeV()) );
    }

    @Override
    public int hashCode() {
        return nodeU.hashCode() + nodeV.hashCode();
    }

    @Override
    public String toString() { return nodeU + " -- " + nodeV; }

}

