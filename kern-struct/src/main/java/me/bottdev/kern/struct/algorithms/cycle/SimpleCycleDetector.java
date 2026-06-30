package me.bottdev.kern.struct.algorithms.cycle;

import me.bottdev.kern.struct.algorithms.traverse.TraversalIterator;
import me.bottdev.kern.struct.algorithms.traverse.Traversals;
import me.bottdev.kern.struct.algorithms.traverse.TraversalStep;
import me.bottdev.kern.struct.graph.EndpointPair;
import me.bottdev.kern.struct.graph.Graph;

import java.util.*;

public class SimpleCycleDetector implements CycleDetector {

    @Override
    public <N, E extends EndpointPair<N>> Optional<CyclePath<N>> detect(Graph<N, E> graph) {

        for (N node : graph.nodes()) {
            Optional<CyclePath<N>> cyclePath = dfs(graph, node);
            if (cyclePath.isPresent()) {
                return cyclePath;
            }
        }

        return Optional.empty();
    }

    private <N, E extends EndpointPair<N>> Optional<CyclePath<N>> dfs(
            Graph<N, E> graph,
            N start
    ) {

        List<N> visited = new ArrayList<>();
        TraversalIterator<N, ?> iterator = Traversals.dfsPreOrder()
                .on(graph)
                .from(start)
                .allowDuplicates(true)
                .iterator();

        while (iterator.hasNext()) {

            TraversalStep<N, ?> step = iterator.next();
            N node = step.node();

            if (visited.contains(node)) {
                CyclePath<N> cyclePath = new CyclePath<>(node, visited);
                return Optional.of(cyclePath);
            }

            visited.add(node);

        }

        return Optional.empty();
    }


}
