package me.bottdev.kern.dependency.graph;

import me.bottdev.kern.dependency.DependencyAware;
import me.bottdev.kern.struct.graph.GraphBuilder;
import me.bottdev.kern.struct.graph.GraphProperties;
import me.bottdev.kern.struct.property.Property;
import me.bottdev.kern.struct.property.PropertyStore;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DependencyGraphBuilder<K, T extends DependencyAware<K>>
        implements GraphBuilder<T, DependencyEndpointPair<K, T>> {

    private final Set<T> nodes = new HashSet<>();
    private final Set<DependencyEndpointPair<K, T>> edges = new HashSet<>();
    private final PropertyStore propertyStore = new PropertyStore();

    public DependencyGraphBuilder() {
        allowsSelfLoops(false);
        allowsParallelEdges(false);
    }

    @Override
    public DependencyGraphBuilder<K, T> addNode(T node) {
        nodes.add(node);
        return this;
    }

    @Override
    public DependencyGraphBuilder<K, T> addEdge(DependencyEndpointPair<K, T> edge) {
        edges.add(edge);
        return this;
    }

    private Map<T, Set<DependencyEndpointPair<K, T>>> buildAdjacencyMap() {
        Map<T, Set<DependencyEndpointPair<K, T>>> map = new HashMap<>();

        nodes.forEach(node -> map.put(node, new HashSet<>()));

        edges.forEach(edge -> {
            map.computeIfAbsent(edge.nodeU(), _ -> new HashSet<>()).add(edge);
            map.computeIfAbsent(edge.nodeV(), _ -> new HashSet<>()).add(edge);
        });

        return map;
    }

    @Override
    public <P> DependencyGraphBuilder<K, T> property(Property<P> property, P value) {
        propertyStore.put(property, value);
        return this;
    }

    @Override
    public DependencyGraphBuilder<K, T> allowsSelfLoops(boolean allowsSelfLoops) {
        propertyStore.put(GraphProperties.ALLOWS_SELF_LOOPS, allowsSelfLoops);
        return this;
    }

    @Override
    public DependencyGraphBuilder<K, T> allowsParallelEdges(boolean allowsParallelEdges) {
        propertyStore.put(GraphProperties.ALLOWS_PARALLEL_EDGES, allowsParallelEdges);
        return this;
    }

    @Override
    public DependencyGraph<K, T> immutable() {
        return new DependencyGraph<>(propertyStore, buildAdjacencyMap());
    }

}
