package me.bottdev.kern.struct.algorithms.sort;

import lombok.RequiredArgsConstructor;
import me.bottdev.kern.struct.algorithms.cycle.CycleDetector;
import me.bottdev.kern.struct.graph.Graph;
import me.bottdev.kern.struct.graph.endpoints.Directed;
import me.bottdev.kern.struct.paths.CyclePath;

import java.util.*;

@RequiredArgsConstructor
public class KahnSorter implements TopologicalSorter {

    private final CycleDetector cycleDetector;


    @Override
    public <N> TopologicalSortResult<N> sort(Graph<N, ? extends Directed<N>> graph)
            throws CircularDependencyException
    {

        TopologicalSortResult.Builder<N> resultBuilder = TopologicalSortResult.builder();

        int processed = 0;
        int size = graph.nodeCount();

        Map<N, Integer> remainingDependencies = new HashMap<>();
        Map<N, Set<N>> dependents = new HashMap<>();
        Queue<N> ready = new ArrayDeque<>();

        graph.nodes().forEach(node -> {

            int outDegree = graph.outDegree(node);
            remainingDependencies.put(node, outDegree);
            if (outDegree == 0) {
                ready.add(node);
            }
            dependents.putIfAbsent(node, new LinkedHashSet<>());

        });

        graph.edges().forEach(edge -> {

            N from = edge.source();
            for (N dependency : graph.successors(from)) {
                dependents.get(from).add(dependency);
            }

        });

        while (!ready.isEmpty()) {

            List<N> layer = new ArrayList<>(ready);
            ready.clear();
            resultBuilder.layer(layer);
            processed += layer.size();

            for (N node : layer) {
                for (N successor : graph.predecessors(node)) {
                    int newOutDegree = remainingDependencies.merge(successor, -1, Integer::sum);
                    if  (newOutDegree == 0) {
                        ready.add(successor);
                    }
                }
            }

        }

        if (processed != size) {

            Optional<CyclePath<N>> pathOptional = cycleDetector.detect(graph);

            if (pathOptional.isPresent()) {
                CyclePath<N> path = pathOptional.get();
                throw new CircularDependencyException(path, "Circular dependency is found in the graph: " + path);
            }

        }

        return resultBuilder.build();

    }

}
