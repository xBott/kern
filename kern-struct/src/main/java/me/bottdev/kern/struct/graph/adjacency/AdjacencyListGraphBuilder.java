package me.bottdev.kern.struct.graph.adjacency;

import me.bottdev.kern.struct.graph.EndpointPair;
import me.bottdev.kern.struct.graph.GraphBuilder;
import me.bottdev.kern.struct.graph.GraphProperties;
import me.bottdev.kern.struct.graph.MutableGraph;
import me.bottdev.kern.struct.property.Property;
import me.bottdev.kern.struct.property.PropertyStore;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AdjacencyListGraphBuilder<N, E extends EndpointPair<N>> implements GraphBuilder<N, E> {

    private final Set<N> nodes = new HashSet<>();
    private final Set<E> edges = new HashSet<>();
    private final PropertyStore propertyStore = new PropertyStore();

    public AdjacencyListGraphBuilder() {
        allowsSelfLoops(false);
        allowsParallelEdges(false);
    }

    @Override
    public AdjacencyListGraphBuilder<N, E> addNode(N node) {
        nodes.add(node);
        return this;
    }

    @Override
    public AdjacencyListGraphBuilder<N, E> addEdge(E edge) {
        edges.add(edge);
        return this;
    }

    private Map<N, Set<E>> buildAdjacencyMap() {
        Map<N, Set<E>> map = new HashMap<>();

        nodes.forEach(node -> map.put(node, new HashSet<>()));

        edges.forEach(edge -> {
            map.computeIfAbsent(edge.nodeU(), _ -> new HashSet<>()).add(edge);
            map.computeIfAbsent(edge.nodeV(), _ -> new HashSet<>()).add(edge);
        });

        return map;
    }

    @Override
    public <P> void property(Property<P> property, P value) {
        propertyStore.put(property, value);
    }

    @Override
    public void allowsSelfLoops(boolean allowsSelfLoops) {
        propertyStore.put(GraphProperties.ALLOWS_SELF_LOOPS, allowsSelfLoops);
    }

    @Override
    public void allowsParallelEdges(boolean allowsParallelEdges) {
        propertyStore.put(GraphProperties.ALLOWS_PARALLEL_EDGES, allowsParallelEdges);
    }

    @Override
    public AdjacencyListGraph<N, E> immutable() {
        return new AdjacencyListGraph<>(propertyStore, buildAdjacencyMap());
    }

    @Override
    public MutableGraph<N, E> mutable() {
        return new MutableAdjacencyListGraph<>(propertyStore, buildAdjacencyMap());
    }

}
