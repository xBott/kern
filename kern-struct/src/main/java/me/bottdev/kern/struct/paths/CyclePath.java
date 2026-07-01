package me.bottdev.kern.struct.paths;

import lombok.NonNull;
import me.bottdev.kern.struct.Path;

import java.util.List;
import java.util.stream.Collectors;

public record CyclePath<N>(
        N node,
        List<N> nodes
) implements Path<N> {

    @Override
    public N start() {
        return node;
    }

    @Override
    public N target() {
        return node;
    }

    @NonNull
    @Override
    public String toString() {
        return nodes.stream().map(N::toString).collect(Collectors.joining(" -> ")) + " ---> " + node;
    }

}
