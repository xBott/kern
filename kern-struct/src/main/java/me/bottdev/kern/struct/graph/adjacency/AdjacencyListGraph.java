package me.bottdev.kern.struct.graph.adjacency;

import me.bottdev.kern.struct.graph.AbstractGraph;
import me.bottdev.kern.struct.graph.EndpointPair;
import me.bottdev.kern.struct.graph.Graph;
import me.bottdev.kern.struct.property.PropertyStore;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/// Immutable adjacency-list graph implementation.
///
/// @param <N> node type
/// @param <E> edge endpoint type
public class AdjacencyListGraph<N, E extends EndpointPair<N>> extends AbstractGraph<N, E> implements Graph<N, E> {

    protected final Map<N, Set<E>> adjacencyMap;

    private Set<N> nodesView;
    private Set<E> cachedEdges;
    private final Map<N, Set<E>> cachedIncidentEdges;

    /// Creates a graph backed by the provided adjacency map.
    public AdjacencyListGraph(PropertyStore propertyStore, Map<N, Set<E>> adjacencyMap) {
        super(propertyStore);
        this.adjacencyMap = adjacencyMap;
        this.nodesView = null;
        this.cachedEdges = null;
        this.cachedIncidentEdges = new HashMap<>();
    }

    protected void invalidateCache() {
        nodesView = null;
        cachedEdges = null;
        cachedIncidentEdges.clear();
    }

    @Override
    public Set<N> nodes() {
        if (nodesView == null) {
            nodesView = Collections.unmodifiableSet(adjacencyMap.keySet());
        }
        return nodesView;
    }

    @Override
    public Set<E> edges() {

        if (cachedEdges == null) {
            Set<E> edges = new LinkedHashSet<>();

            adjacencyMap.forEach((node, value) -> {
                for (E edge : value) {
                    if (!edge.isDirected() || edge.nodeU().equals(node)) {
                        edges.add(edge);
                    }
                }
            });

            cachedEdges = Collections.unmodifiableSet(edges);
        }

        return cachedEdges;
    }

    @Override
    public Set<E> incidentEdges(N node) {
        Set<E> edges = adjacencyMap.get(node);
        if (edges == null) return Collections.emptySet();
        return cachedIncidentEdges.computeIfAbsent(node, ignored -> Collections.unmodifiableSet(edges));
    }

    @Override
    public boolean hasNode(N node) {
        return adjacencyMap.containsKey(node);
    }

    @Override
    public boolean hasEdge(E edge) {
        return edges().contains(edge);
    }

    @Override
    public boolean hasEdgeConnecting(N nodeU, N nodeV) {
        return false;
    }

    protected Map<N, Set<E>> deepCopyAdjacencyMap() {
        Map<N, Set<E>> copy = new LinkedHashMap<>();
        for (Map.Entry<N, Set<E>> entry : adjacencyMap.entrySet()) {
            copy.put(entry.getKey(), new LinkedHashSet<>(entry.getValue()));
        }
        return copy;
    }

    @Override
    public MutableAdjacencyListGraph<N, E> toMutable() {
        return new MutableAdjacencyListGraph<>(propertyStore().copy(), deepCopyAdjacencyMap());
    }

    protected void validateNodeSubSet(Set<N> subNodes) {
        for (N subNode : subNodes) {
            if (!hasNode(subNode))
                throw new NoSuchElementException("Node not found in the graph: " + subNode);
        }
    }

    protected Map<N, Set<E>> buildInducedMap(Set<N> subNodes) {

        Map<N, Set<E>> filteredMap = new LinkedHashMap<>();

        for (N node : subNodes) {
            Set<E> filteredEdges = new LinkedHashSet<>();

            for (E edge : incidentEdges(node)) {
                if (subNodes.contains(edge.nodeU()) && subNodes.contains(edge.nodeV()))
                    filteredEdges.add(edge);
            }

            filteredMap.put(node, filteredEdges);
        }

        return filteredMap;

    }

    protected Map<N, Set<E>> buildFilteredEdgeMap(Predicate<E> predicate) {

        Map<N, Set<E>> filteredMap = new LinkedHashMap<>();

        for (N node : nodes()) {
            filteredMap.put(node, new LinkedHashSet<>());
        }

        for (E edge : edges()) {
            if (predicate.test(edge)) {
                filteredMap.get(edge.nodeU()).add(edge);
                filteredMap.get(edge.nodeV()).add(edge);
            }
        }

        return filteredMap;
    }

    @Override
    public AdjacencyListGraph<N, E> subgraph(Set<N> subNodes) {
        validateNodeSubSet(subNodes);
        Map<N, Set<E>> filteredMap = buildInducedMap(subNodes);
        return new AdjacencyListGraph<>(propertyStore().copy(), filteredMap);
    }

    @Override
    public AdjacencyListGraph<N, E> filterNodes(Predicate<N> predicate) {
        Set<N> selected = nodes().stream()
                .filter(predicate)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        Map<N, Set<E>> filteredMap = buildInducedMap(selected);
        return new AdjacencyListGraph<>(propertyStore().copy(), filteredMap);
    }

    @Override
    public AdjacencyListGraph<N, E> filterEdges(Predicate<E> predicate) {
        Map<N, Set<E>> filteredMap = buildFilteredEdgeMap(predicate);
        return new AdjacencyListGraph<>(propertyStore().copy(), filteredMap);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("AdjacencyListGraph{\n");
        for (E edge : edges()) {
            sb.append(edge).append("\n");
        }
        sb.append('}');
        return sb.toString();
    }
}
