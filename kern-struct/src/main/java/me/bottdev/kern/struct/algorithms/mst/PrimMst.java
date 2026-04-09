package me.bottdev.kern.struct.algorithms.mst;

import me.bottdev.kern.struct.graph.EndpointPair;
import me.bottdev.kern.struct.graph.Graph;
import me.bottdev.kern.struct.graph.GraphBuilder;
import me.bottdev.kern.struct.graph.Weighted;

import java.util.*;

public class PrimMst {

    public <N, E extends EndpointPair<N> & Weighted> Optional<Graph<N, E>> apply(
            Graph<N, E> graph,
            GraphBuilder<N, E> mstBuilder
    ) {
        if (graph.nodes().isEmpty()) return Optional.empty();

        Set<N> visited = new HashSet<>();
        PriorityQueue<E> prioritizedEdges = new PriorityQueue<>(Comparator.comparingDouble(Weighted::weight));

        N startNode = graph.nodes().iterator().next();
        visited.add(startNode);
        prioritizedEdges.addAll(graph.outEdges(startNode));

        while (!prioritizedEdges.isEmpty()) {
            E edge = prioritizedEdges.poll();

            N nodeU = edge.nodeU();
            N nodeV = edge.nodeV();

            N next = visited.contains(nodeU) ? nodeV : nodeU;

            if (visited.contains(next)) continue;

            mstBuilder.addEdge(edge);
            visited.add(next);

            for (E e : graph.outEdges(next)) {
                N target = e.nodeU().equals(next) ? e.nodeV() : e.nodeU();
                if (!visited.contains(target)) {
                    prioritizedEdges.add(e);
                }
            }
        }

        if (visited.size() != graph.nodes().size()) {
            return Optional.empty();
        }

        return Optional.of(mstBuilder.immutable());
    }

}
