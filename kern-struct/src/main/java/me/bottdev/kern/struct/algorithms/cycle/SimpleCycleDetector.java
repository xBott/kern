package me.bottdev.kern.struct.algorithms.cycle;

import me.bottdev.kern.struct.algorithms.traverse.GraphTraversal;
import me.bottdev.kern.struct.algorithms.traverse.TraversalOrders;
import me.bottdev.kern.struct.algorithms.traverse.TraversalStep;
import me.bottdev.kern.struct.graph.EndpointPair;
import me.bottdev.kern.struct.graph.Graph;

import java.util.*;

public class SimpleCycleDetector<N> implements CycleDetector<N> {

    @Override
    public <E extends EndpointPair<N>> Optional<CyclePath<N>> detectFirst(Graph<N, E> graph) {

        GraphTraversal<N, E> traversal = new GraphTraversal<N, E>()
                .order(TraversalOrders.dfs())
                .allowDuplicates(true);

        for (N node : graph.nodes()) {
            Optional<CyclePath<N>> cyclePath = dfs(graph, traversal, node);
            if (cyclePath.isPresent()) {
                return cyclePath;
            }
        }

        return Optional.empty();
    }

    @Override
    public <E extends EndpointPair<N>> CycleResult<N> detectAll(Graph<N, E> graph) {

        return detectFirst(graph)
                .map(cyclePath -> new CycleResult<>(Set.of(cyclePath)))
                .orElseGet(() -> new CycleResult<>(Collections.emptySet()));

    }

    private <E extends EndpointPair<N>> Optional<CyclePath<N>> dfs(
            Graph<N, E> graph,
            GraphTraversal<N, E> traversal,
            N start
    ) {

        List<N> visited = new ArrayList<>();
        Iterator<TraversalStep<N, E>> iterator = traversal.iterator(graph, start);

        while (iterator.hasNext()) {

            TraversalStep<N, E> step = iterator.next();
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
