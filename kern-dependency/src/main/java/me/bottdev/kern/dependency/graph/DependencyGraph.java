package me.bottdev.kern.dependency.graph;

import me.bottdev.kern.dependency.DependencyAware;
import me.bottdev.kern.struct.graph.adjacency.AdjacencyListGraph;
import me.bottdev.kern.struct.property.PropertyStore;

import java.util.Map;
import java.util.Set;

public class DependencyGraph<K, T extends DependencyAware<K>>
        extends AdjacencyListGraph<T, DependencyEndpointPair<K, T>> {

    public DependencyGraph(PropertyStore propertyStore, Map<T, Set<DependencyEndpointPair<K, T>>> adjacencyMap) {
        super(propertyStore, adjacencyMap);
    }

}
