package me.bottdev.kern.struct.algorithms.cycle;

import me.bottdev.kern.struct.algorithms.traverse.*;
import me.bottdev.kern.struct.graph.EndpointPair;
import me.bottdev.kern.struct.graph.Graph;
import me.bottdev.kern.struct.paths.CyclePath;

import java.util.*;

public class SimpleCycleDetector implements CycleDetector {

    @Override
    public <N, E extends EndpointPair<N>> Optional<CyclePath<N>> detect(
            Graph<N, E> graph
    ) {
        Set<N> visited = new HashSet<>();
        Set<N> visiting = new HashSet<>();
        List<N> path = new ArrayList<>();

        for (N node : graph.nodes()) {

            if (visited.contains(node)) {
                continue;
            }

            Optional<CyclePath<N>> cycle = dfs(
                    graph,
                    node,
                    visited,
                    visiting,
                    path
            );

            if (cycle.isPresent()) {
                return cycle;
            }
        }

        return Optional.empty();
    }

    private <N, E extends EndpointPair<N>> Optional<CyclePath<N>> dfs(
            Graph<N, E> graph,
            N current,
            Set<N> visited,
            Set<N> visiting,
            List<N> path
    ) {

        if (visiting.contains(current)) {

            int cycleStart = path.indexOf(current);

            List<N> cycle = new ArrayList<>(
                    path.subList(cycleStart, path.size())
            );

            cycle.add(current);

            return Optional.of(
                    new CyclePath<>(cycle)
            );
        }

        if (visited.contains(current)) {
            return Optional.empty();
        }

        visiting.add(current);
        path.add(current);

        for (N successor : graph.successors(current)) {

            Optional<CyclePath<N>> result = dfs(
                    graph,
                    successor,
                    visited,
                    visiting,
                    path
            );

            if (result.isPresent()) {
                return result;
            }
        }

        path.removeLast();
        visiting.remove(current);
        visited.add(current);

        return Optional.empty();
    }

}