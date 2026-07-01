package me.bottdev.kern.struct.paths;

import me.bottdev.kern.struct.Path;

import java.util.List;
import java.util.stream.Collectors;

public record WeightedPath<N>(
        N start,
        N target,
        double distance,
        List<N> nodes
) implements Path<N> {

    @Override
    public String toString() {
        return nodes.stream().map(N::toString).collect(Collectors.joining(" -> ")) + "(" + distance + ")";
    }

}
