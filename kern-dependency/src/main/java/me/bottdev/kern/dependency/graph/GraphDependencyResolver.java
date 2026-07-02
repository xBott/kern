package me.bottdev.kern.dependency.graph;

import lombok.RequiredArgsConstructor;
import me.bottdev.kern.dependency.*;
import me.bottdev.kern.dependency.exceptions.DependencyException;
import me.bottdev.kern.dependency.exceptions.MissingDependencyException;
import me.bottdev.kern.struct.algorithms.sort.CircularDependencyException;
import me.bottdev.kern.struct.algorithms.sort.TopologicalSortResult;
import me.bottdev.kern.struct.algorithms.sort.TopologicalSorter;
import me.bottdev.kern.struct.graph.EndpointPairs;
import me.bottdev.kern.struct.graph.Graph;
import me.bottdev.kern.struct.graph.adjacency.AdjacencyListGraphBuilder;
import me.bottdev.kern.struct.graph.endpoints.Directed;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/// Implementation of [DependencyResolver] that uses graph and Topological Sort.
@RequiredArgsConstructor
public class GraphDependencyResolver implements DependencyResolver {

    private final TopologicalSorter sorter;

    @Override
    public <K, T extends DependencyAware<K>> ResolutionResult<T> resolve(DependentContainer<K, T> dependentContainer)
            throws DependencyException, CircularDependencyException
    {

        Map<K, T> dependentMap = buildMap(dependentContainer);

        Graph<K, Directed<K>> graph = buildGraph(dependentContainer, dependentMap);

        TopologicalSortResult<K> sortedKeys = sorter.sort(graph);

        return convertSortResult(sortedKeys, dependentMap);

    }

    private <K, T extends DependencyAware<K>> Map<K, T> buildMap(DependentContainer<K, T> dependentContainer) {
        Map<K, T> dependentMap = new HashMap<>();
        dependentContainer.dependents().forEach(dependent ->
                dependentMap.put(dependent.dependencyKey(), dependent)
        );

        return dependentMap;
    }

    private <K, T extends DependencyAware<K>> Graph<K, Directed<K>> buildGraph(
            DependentContainer<K, T> dependentContainer,
            Map<K, T> dependentMap
    ) throws DependencyException {

        AdjacencyListGraphBuilder<K, Directed<K>> builder = new AdjacencyListGraphBuilder<>();

        for (T dependent : dependentContainer.dependents()) {

            K dependentKey = dependent.dependencyKey();
            builder.addNode(dependentKey);

            for (DependencyRequest<K> request : dependent.getDependencies()) {

                DependencyLink link = request.link();
                if (link == DependencyLink.OPTIONAL) continue;

                K dependencyKey = request.key();
                T dependency = dependentMap.get(dependencyKey);

                if (dependency == null) {
                    throw new MissingDependencyException(
                            dependent,
                            dependencyKey,
                            "Dependency " + dependencyKey + " of " + dependentKey + " does not exist."
                    );
                }

                Directed<K> edge = switch (request.order()) {
                    case BEFORE -> EndpointPairs.directed(dependencyKey, dependentKey);
                    case AFTER -> EndpointPairs.directed(dependentKey, dependencyKey);
                };

                builder.addEdge(edge);

            }

        }

        return builder.immutable();
    }

    private <K, T extends DependencyAware<K>> ResolutionResult<T> convertSortResult(
            TopologicalSortResult<K> sortedKeys,
            Map<K, T> dependentMap
    ) {


        List<List<T>> layers = sortedKeys.layers().stream()
                .map(layer -> layer.stream().map(dependentMap::get).toList())
                .toList();

        List<T> ordered = layers.stream().flatMap(Collection::stream).toList();

        return new ResolutionResult<>(ordered, layers);

    }

}
