package me.bottdev.kern.struct.graph.adjacency;

import me.bottdev.kern.struct.graph.AbstractGraph;
import me.bottdev.kern.struct.graph.EndpointPair;
import me.bottdev.kern.struct.property.PropertyStore;

import java.util.*;

public class AdjacencyListGraph<N, E extends EndpointPair<N>> extends AbstractGraph<N, E> {

    protected final Map<N, Set<E>> adjacencyMap;
    protected Set<E> cachedEdges;
    protected Map<N, Set<E>> cachedIncident;

    public AdjacencyListGraph(PropertyStore propertyStore, Map<N, Set<E>> adjacencyMap) {
        super(propertyStore);
        this.adjacencyMap = adjacencyMap;
        cachedEdges = null;
        cachedIncident = new HashMap<>();
    }

    protected void invalidateCache() {
        cachedEdges = null;
        cachedIncident = new HashMap<>();
    }

    @Override
    public Set<N> nodes() {
        return Collections.unmodifiableSet(adjacencyMap.keySet());
    }

    @Override
    public Set<E> edges() {

        if (cachedEdges == null) {
            Set<E> edges = new HashSet<>();

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
        if (edges == null) return Set.of();
        return Collections.unmodifiableSet(edges);
    }


}
