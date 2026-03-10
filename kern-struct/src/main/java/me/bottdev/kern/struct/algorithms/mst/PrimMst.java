package me.bottdev.kern.struct.algorithms.mst;

import me.bottdev.kern.struct.graph.EndpointPair;
import me.bottdev.kern.struct.graph.Graph;
import me.bottdev.kern.struct.graph.GraphBuilder;
import me.bottdev.kern.struct.graph.Weighted;

import java.util.*;

public class PrimMst {

    <N, E extends EndpointPair<N> & Weighted> Optional<Graph<N, E>> apply(
            Graph<N, E> graph,
            GraphBuilder<N, E> mstBuilder
    ) {

        Set<N> visited = new HashSet<>();
        PriorityQueue<E> prioritizedEdges = new PriorityQueue<>(Comparator.comparingDouble(Weighted::weight));

        Optional<N> startNodeOptional = graph.nodes().stream().findFirst();
        if (startNodeOptional.isEmpty()) return Optional.empty();
        N startNode = startNodeOptional.get();

        visited.add(startNode);
        prioritizedEdges.addAll(graph.outEdges(startNode));

        while (!prioritizedEdges.isEmpty()) {

            E edge = prioritizedEdges.poll();

            N nodeU = edge.nodeU();
            N nodeV = edge.nodeV();

            N next = visited.contains(nodeU) ? nodeV : nodeU;
            if (visited.contains(next)) continue;

            visited.add(nodeV);
            mstBuilder.addEdge(edge);


            visited.add(next);
            mstBuilder.addEdge(edge);

            prioritizedEdges.addAll(graph.outEdges(next));

        }

        Graph<N, E> mst = mstBuilder.immutable();
        return Optional.of(mst);

    }

}
