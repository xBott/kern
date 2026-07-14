package me.bottdev.kern.struct.graph.adjacency;

import me.bottdev.kern.struct.graph.EndpointPair;
import me.bottdev.kern.struct.graph.MutableGraph;
import me.bottdev.kern.struct.graph.exceptions.GraphParallelEdgeException;
import me.bottdev.kern.struct.graph.exceptions.GraphSelfLoopException;
import me.bottdev.kern.struct.property.PropertyStore;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/// Mutable adjacency-list graph implementation.
///
/// @param <N> node type
/// @param <E> edge endpoint type
public class MutableAdjacencyListGraph<N, E extends EndpointPair<N>>
        extends AdjacencyListGraph<N, E> implements MutableGraph<N, E> {

    /// Creates a mutable graph backed by the provided adjacency map.
    public MutableAdjacencyListGraph(PropertyStore propertyStore, Map<N, Set<E>> adjacencyMap) {
        super(propertyStore, adjacencyMap);
    }


    @Override
    public boolean addNode(N node) {
        if (hasNode(node)) return false;
        adjacencyMap.computeIfAbsent(node, ignored -> new LinkedHashSet<>());
        invalidateCache();
        return true;
    }

    @Override
    public boolean removeNode(N node) {
        if (!hasNode(node)) return false;
        adjacencyMap.remove(node);
        adjacencyMap.forEach((ignored, edges) ->
                edges.removeIf(edge -> edge.hasEndpoint(node))
        );
        invalidateCache();
        return true;
    }

    @Override
    public void addEdge(E edge) {
        N nodeU = edge.nodeU();
        N nodeV = edge.nodeV();

        if (nodeU.equals(nodeV) && !allowsSelfLoops())
            throw new GraphSelfLoopException("Edge \"" + nodeU + " -> " + nodeV + "\" is not allowed.");
        if (hasEdgeConnecting(nodeU, nodeV) && !allowsParallelEdges())
            throw new GraphParallelEdgeException("Edge \"" + nodeU + " -> " + nodeV + "\" is not allowed.");

        addNode(nodeU);
        addNode(nodeV);

        boolean added = adjacencyMap.get(nodeU).add(edge);
        added |= adjacencyMap.get(nodeV).add(edge);

        if (added) {
            invalidateCache();
        }
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

        Set<E> edgesV = adjacencyMap.get(nodeV);
        if (edgesV != null) {
            removed |= edgesV.remove(edge);
        }

        if (removed) {
            invalidateCache();
        }
        return removed;
    }

    @Override
    public int addAllNodes(Collection<N> nodes) {

        int totalAdded = 0;

        for (N node : nodes) {
            if (adjacencyMap.containsKey(node)) continue;
            adjacencyMap.put(node, new LinkedHashSet<>());
            totalAdded++;
        }

        if (totalAdded > 0) invalidateCache();

        return totalAdded;
    }

    private void validateEdgeBatch(Collection<E> edges) {

        Map<N, Set<N>> virtualNeighbors = new HashMap<>();

        for (E edge : edges) {
            N nodeU = edge.nodeU();
            N nodeV = edge.nodeV();

            if (nodeU.equals(nodeV) && !allowsSelfLoops())
                throw new GraphSelfLoopException("Edge \"" + nodeU + " -> " + nodeV + "\" is not allowed.");

            if (!allowsParallelEdges()) {
                boolean existsAlready = hasEdgeConnecting(nodeU, nodeV);
                boolean existsInBatch = virtualNeighbors.getOrDefault(nodeU, Set.of()).contains(nodeV);
                if (existsAlready || existsInBatch)
                    throw new GraphParallelEdgeException("Edge \"" + nodeU + " -> " + nodeV + "\" is not allowed.");
            }

            virtualNeighbors.computeIfAbsent(nodeU, k -> new HashSet<>()).add(nodeV);
            virtualNeighbors.computeIfAbsent(nodeV, k -> new HashSet<>()).add(nodeU);
        }

    }

    @Override
    public int addAllEdges(Collection<E> edges) {

        validateEdgeBatch(edges);

        int totalAdded = 0;

        for (E edge : edges) {

            N nodeU = edge.nodeU();
            N nodeV = edge.nodeV();

            addNode(nodeU);
            addNode(nodeV);

            boolean added = adjacencyMap.get(nodeU).add(edge);
            added |= adjacencyMap.get(nodeV).add(edge);

            if (added) totalAdded++;

        }

        if (totalAdded > 0) invalidateCache();

        return totalAdded;
    }

    @Override
    public int removeAllNodes(Collection<N> nodes) {

        int totalRemoved = 0;

        for (N node : nodes) {
            if (adjacencyMap.remove(node) != null) {
                totalRemoved++;
            }
        }

        if (totalRemoved > 0) {
            Set<N> removed = new HashSet<>(nodes);
            for (Set<E> incidentEdges : adjacencyMap.values()) {
                incidentEdges.removeIf(edge ->
                        removed.contains(edge.nodeU()) || removed.contains(edge.nodeV())
                );
            }
            invalidateCache();
        }

        return totalRemoved;
    }

    @Override
    public int removeAllEdges(Collection<E> edges) {

        int totalRemoved = 0;

        for (E edge : edges) {
            N nodeU = edge.nodeU();
            N nodeV = edge.nodeV();

            boolean removed = false;

            Set<E> edgesU = adjacencyMap.get(nodeU);
            if (edgesU != null) {
                removed |= edgesU.remove(edge);
            }

            Set<E> edgesV = adjacencyMap.get(nodeV);
            if (edgesV != null) {
                removed |= edgesV.remove(edge);
            }

            if (removed) totalRemoved++;
        }

        if (totalRemoved > 0) invalidateCache();

        return totalRemoved;
    }

    @Override
    public void clear() {
        adjacencyMap.clear();
        invalidateCache();
    }

    @Override
    public AdjacencyListGraph<N, E> toImmutable() {
        return new AdjacencyListGraph<>(propertyStore().copy(), deepCopyAdjacencyMap());
    }

    @Override
    public MutableAdjacencyListGraph<N, E> copy() {
        return new MutableAdjacencyListGraph<>(propertyStore().copy(), deepCopyAdjacencyMap());
    }

    @Override
    public MutableAdjacencyListGraph<N, E> subgraph(Set<N> subNodes) {
        validateNodeSubSet(subNodes);
        Map<N, Set<E>> filteredMap = buildInducedMap(subNodes);
        return new MutableAdjacencyListGraph<>(propertyStore().copy(), filteredMap);
    }

    @Override
    public MutableAdjacencyListGraph<N, E> filterNodes(Predicate<N> predicate) {
        Set<N> selected = nodes().stream()
                .filter(predicate)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        Map<N, Set<E>> filteredMap = buildInducedMap(selected);
        return new MutableAdjacencyListGraph<>(propertyStore().copy(), filteredMap);
    }

    @Override
    public MutableAdjacencyListGraph<N, E> filterEdges(Predicate<E> predicate) {
        Map<N, Set<E>> filteredMap = buildFilteredEdgeMap(predicate);
        return new MutableAdjacencyListGraph<>(propertyStore().copy(), filteredMap);
    }

}
