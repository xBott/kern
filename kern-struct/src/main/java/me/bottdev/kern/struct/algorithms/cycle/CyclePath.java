package me.bottdev.kern.struct.algorithms.cycle;

import me.bottdev.kern.struct.PathResult;

import java.util.List;
import java.util.stream.Collectors;

public record CyclePath<N>(
        N node,
        List<N> nodes
) implements PathResult<N> {

    @Override
    public N start() {
        return node;
    }

    @Override
    public N target() {
        return node;
    }

    @Override
    public String toString() {
        return nodes.stream().map(N::toString).collect(Collectors.joining(" -> ")) + " ---> " + node;
    }

}
