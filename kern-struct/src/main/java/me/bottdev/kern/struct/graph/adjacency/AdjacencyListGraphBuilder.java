package me.bottdev.kern.struct.graph.adjacency;

import me.bottdev.kern.struct.graph.*;
import me.bottdev.kern.struct.property.Property;
import me.bottdev.kern.struct.property.PropertyStore;

import java.util.*;

/// Builder for adjacency-list graph instances.
///
/// @param <N> node type
/// @param <E> edge endpoint type
public class AdjacencyListGraphBuilder<N, E extends EndpointPair<N>>
        implements GraphBuilder<N, E>, MutableGraphBuilder<N, E>
{

    private final Set<N> nodes = new LinkedHashSet<>();
    private final Set<E> edges = new LinkedHashSet<>();
    private final PropertyStore propertyStore = new PropertyStore();

    /// Creates a builder with self-loops and parallel edges disabled.
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
        Map<N, Set<E>> map = new LinkedHashMap<>();

        nodes.forEach(node -> map.put(node, new LinkedHashSet<>()));

        edges.forEach(edge -> {
            map.computeIfAbsent(edge.nodeU(), ignored -> new LinkedHashSet<>()).add(edge);
            map.computeIfAbsent(edge.nodeV(), ignored -> new LinkedHashSet<>()).add(edge);
        });

        return map;
    }

    @Override
    public <P> AdjacencyListGraphBuilder<N, E> property(Property<P> property, P value) {
        propertyStore.put(property, value);
        return this;
    }

    @Override
    public AdjacencyListGraphBuilder<N, E> allowsSelfLoops(boolean allowsSelfLoops) {
        propertyStore.put(GraphProperties.ALLOWS_SELF_LOOPS, allowsSelfLoops);
        return this;
    }

    @Override
    public AdjacencyListGraphBuilder<N, E> allowsParallelEdges(boolean allowsParallelEdges) {
        propertyStore.put(GraphProperties.ALLOWS_PARALLEL_EDGES, allowsParallelEdges);
        return this;
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
