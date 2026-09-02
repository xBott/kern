package me.bottdev.kern.dependency.simple.graph;

import lombok.NonNull;
import me.bottdev.kern.commons.diagnostic.DiagnosticSeverity;
import me.bottdev.kern.commons.diagnostic.DiagnosticsBuilder;
import me.bottdev.kern.commons.diagnostic.ListDiagnostics;
import me.bottdev.kern.commons.wrapper.DiagnosticResult;
import me.bottdev.kern.dependency.*;

import me.bottdev.kern.struct.algorithms.sort.CircularDependencyException;
import me.bottdev.kern.struct.algorithms.sort.TopologicalSortResult;
import me.bottdev.kern.struct.algorithms.sort.TopologicalSorter;
import me.bottdev.kern.struct.graph.EndpointPairs;
import me.bottdev.kern.struct.graph.Graph;
import me.bottdev.kern.struct.graph.adjacency.AdjacencyListGraphBuilder;
import me.bottdev.kern.struct.graph.endpoints.Directed;
import me.bottdev.kern.struct.paths.CyclePath;


import java.util.Collection;
import java.util.List;

/// Implementation of [DependencyResolver] that uses a graph and Topological Sort.
public class GraphDependencyResolver implements DependencyResolver {

    private final TopologicalSorter sorter;

    public GraphDependencyResolver(@NonNull TopologicalSorter sorter) {
        this.sorter = sorter;
    }

    /// Builds the graph, skipping edges for missing dependencies rather than throwing.
    /// Any missing dependency is appended to `diagnostics` instead.
    private <K, T extends DependencyAware<K>> Graph<K, Directed<K>> buildGraph(
            DependentContainer<K, T> dependentContainer,
            DiagnosticsBuilder<DependencyDiagnostic> diagnosticsBuilder
    ) {

        AdjacencyListGraphBuilder<K, Directed<K>> builder = new AdjacencyListGraphBuilder<>();

        for (T dependent : dependentContainer.values()) {

            K dependentKey = dependent.dependencyKey();
            builder.addNode(dependentKey);

            for (DependencyRequest<K> request : dependent.getDependencies()) {

                K dependencyKey = request.key();
                T dependency = dependentContainer.get(dependencyKey);
                DependencyLink link = request.link();

                if (dependency == null) {
                    if (link == DependencyLink.REQUIRED) {
                        diagnosticsBuilder.append(new DependencyDiagnostic.Missing<>(dependentKey, dependencyKey));
                    }
                    continue;
                }


                Directed<K> edge = switch (request.order()) {
                    case AFTER -> EndpointPairs.directed(dependentKey, dependencyKey);
                    case BEFORE -> EndpointPairs.directed(dependencyKey, dependentKey);
                };

                builder.addEdge(edge);

            }

        }

        return builder.immutable();
    }


    /// Attempts the topological sort, converting a thrown [CircularDependencyException]
    /// into a [DependencyDiagnostic.Circular] instead of propagating it.
    private <K, T extends DependencyAware<K>> DiagnosticResult<ResolutionResult<K, T>, DependencyDiagnostic> sortAndConvert(
            Graph<K, Directed<K>> graph,
            DependentContainer<K, T> dependentContainer,
            DiagnosticsBuilder<DependencyDiagnostic> diagnosticsBuilder
    ) {
        try {
            TopologicalSortResult<K> sortedKeys = sorter.sort(graph);

            List<List<T>> layers = sortedKeys.layers().stream()
                    .map(layer -> layer.stream().map(dependentContainer::get).toList())
                    .toList();

            List<T> ordered = layers.stream().flatMap(Collection::stream).toList();
            ResolutionResult<K, T> resolutionResult = new ResolutionResult<>(ordered, layers);

            return DiagnosticResult.success(resolutionResult);

        } catch (CircularDependencyException ex) {
            CyclePath<K> cycle = ex.getCyclePath();
            diagnosticsBuilder.append(new DependencyDiagnostic.Circular<>(cycle));
            return DiagnosticResult.failure(diagnosticsBuilder.build());

        }

    }

    @Override
    public <K, T extends DependencyAware<K>> DiagnosticResult<ResolutionResult<K, T>, DependencyDiagnostic> resolve(
            @NonNull DependentContainer<K, T> dependentContainer
    ) {

        if (dependentContainer.isEmpty()) return DiagnosticResult.success(ResolutionResult.empty());

        DiagnosticsBuilder<DependencyDiagnostic> diagnosticsBuilder = ListDiagnostics.builder();

        Graph<K, Directed<K>> graph = buildGraph(dependentContainer, diagnosticsBuilder);

        if (diagnosticsBuilder.has(DiagnosticSeverity.ERROR)) {
            return DiagnosticResult.failure(diagnosticsBuilder.build());
        }

        return sortAndConvert(graph, dependentContainer, diagnosticsBuilder);
    }



}