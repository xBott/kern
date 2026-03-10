package me.bottdev.kern.struct.algorithms.shortestpath;

import me.bottdev.kern.struct.PathResult;

import java.util.List;
import java.util.stream.Collectors;

public record DijkstraPath<N>(
        N start,
        N target,
        double distance,
        List<N> nodes
) implements PathResult<N> {

    @Override
    public String toString() {
        return nodes.stream().map(N::toString).collect(Collectors.joining(" -> "));
    }

}
