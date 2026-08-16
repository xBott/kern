package me.bottdev.kern.struct.paths;

import lombok.NonNull;
import me.bottdev.kern.struct.Path;

import java.util.List;
import java.util.stream.Collectors;

public record CyclePath<N>(
        List<N> nodes
) implements Path<N> {

    public N node() {
        return nodes.getFirst();
    }

    @Override
    public N start() {
        return node();
    }

    @Override
    public N target() {
        return node();
    }

    @NonNull
    @Override
    public String toString() {
        return nodes.stream()
                .limit(nodes.size() -1)
                .map(N::toString)
                .collect(Collectors.joining(" -> ")) + " ---> " + node();
    }

}
