package me.bottdev.kern.struct.graph.adjacency;

import me.bottdev.kern.struct.graph.EndpointPair;
import me.bottdev.kern.struct.graph.MutableGraph;
import me.bottdev.kern.struct.property.PropertyStore;

import java.util.*;

public class MutableAdjacencyListGraph<N, E extends EndpointPair<N>>
        extends AdjacencyListGraph<N, E> implements MutableGraph<N, E> {

    public MutableAdjacencyListGraph(PropertyStore propertyStore, Map<N, Set<E>> adjacencyMap) {
        super(propertyStore, adjacencyMap);
    }


    @Override
    public boolean addNode(N node) {
        if (hasNode(node)) return false;
        adjacencyMap.computeIfAbsent(node, n -> new HashSet<>());
        invalidateCache();
        return true;
    }

    @Override
    public boolean removeNode(N node) {
        if (!hasNode(node)) return false;
        adjacencyMap.remove(node);
        adjacencyMap.forEach((_, edges) ->
                edges.removeIf(edge -> edge.hasEndpoint(node))
        );
        invalidateCache();
        return true;
    }

    @Override
    public boolean addEdge(E edge) {
        N nodeU = edge.nodeU();
        N nodeV = edge.nodeV();

        if (nodeU.equals(nodeV) && !allowsSelfLoops()) return false;
        if (hasEdgeConnecting(nodeU, nodeV) && !allowsParallelEdges()) return false;

        if (!hasNode(nodeU)) addNode(nodeU);
        if (!hasNode(nodeV)) addNode(nodeV);

        boolean added = adjacencyMap.get(nodeU).add(edge);

        if (!edge.isDirected()) {
            adjacencyMap.get(nodeV).add(edge);
        }

        if (added) {
            invalidateCache();
        }

        return added;

    }

    @Override
    public boolean removeEdge(E edge) {
        N nodeU = edge.nodeU();
        N nodeV = edge.nodeV();

        if (!hasNode(nodeU) || !hasNode(nodeV)) return false;

        boolean removed = false;

        Set<E> edgesU = adjacencyMap.get(nodeU);
        if (edgesU != null) {
            removed |= edgesU.remove(edge);
        }

        if (!edge.isDirected()) {
            Set<E> edgesV = adjacencyMap.get(nodeV);
            if (edgesV != null) {
                removed |= edgesV.remove(edge);
            }
        }

        return removed;
    }

}
