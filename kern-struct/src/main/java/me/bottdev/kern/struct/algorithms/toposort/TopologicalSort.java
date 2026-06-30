package me.bottdev.kern.struct.algorithms.toposort;

import lombok.RequiredArgsConstructor;
import me.bottdev.kern.commons.Result;
import me.bottdev.kern.struct.algorithms.cycle.CycleDetector;
import me.bottdev.kern.struct.algorithms.cycle.CyclePath;
import me.bottdev.kern.struct.graph.EndpointPair;
import me.bottdev.kern.struct.graph.Graph;

import java.util.*;

@RequiredArgsConstructor
public class TopologicalSort {

    private final CycleDetector cycleDetector;

    public <N> Result<List<N>, CyclePath<N>> sort(Graph<N, ? extends EndpointPair<N>> graph) {

        int size = graph.nodeCount();

        Map<N, Integer> inDegrees = new HashMap<>();
        Queue<N> queue = new ArrayDeque<>();
        List<N> sortedNodes = new ArrayList<>(size);

        graph.nodes().forEach(node -> {
            int inDegree = graph.inDegree(node);
            inDegrees.put(node, inDegree);
            if (inDegree == 0) {
                queue.add(node);
            }
        });

        while (!queue.isEmpty()) {
            N node = queue.poll();
            sortedNodes.add(node);

            for (N successor : graph.successors(node)) {
                int newInDegree = inDegrees.merge(successor, -1, Integer::sum);
                if  (newInDegree == 0) {
                    queue.add(successor);
                }
            }

        }

        if (sortedNodes.size() != size) {
            Optional<CyclePath<N>> cycleOptional = cycleDetector.detect(graph);
            if (cycleOptional.isPresent()) return Result.err(cycleOptional.get());

        }

        return Result.ok(sortedNodes);

    }

}
