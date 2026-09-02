package me.bottdev.kern.dependency.graph;

import lombok.NonNull;
import me.bottdev.kern.commons.diagnostic.DiagnosticType;
import me.bottdev.kern.commons.diagnostic.DiagnosticsBuilder;
import me.bottdev.kern.commons.diagnostic.ListDiagnostics;
import me.bottdev.kern.commons.wrapper.DiagnosticResult;
import me.bottdev.kern.dependency.*;
import me.bottdev.kern.struct.algorithms.sort.CircularDependencyException;
import me.bottdev.kern.struct.algorithms.sort.TopologicalSortResult;
import me.bottdev.kern.struct.algorithms.sort.TopologicalSorter;
import me.bottdev.kern.struct.graph.EndpointPairs;
import me.bottdev.kern.struct.graph.endpoints.Directed;
import me.bottdev.kern.struct.paths.CyclePath;

import java.util.*;

/// Implementation of [StatefulDependencyResolver] that uses a graph and Topological Sort.
public class GraphStatefulDependencyResolver<K, T extends DependencyAware<K>> implements StatefulDependencyResolver<K, T> {

    private final TopologicalSorter sorter;
    private final GraphDependencyResolverState<K, T> state;

    public GraphStatefulDependencyResolver(@NonNull TopologicalSorter sorter) {
        this.sorter = sorter;
        this.state = new GraphDependencyResolverState<>();
    }

    @Override
    public @NonNull DependencyResolverState<K, T> state() {
        return state;
    }

    private void validateDuplicates(
            DependentContainer<K, T> dependentContainer,
            DiagnosticsBuilder<DependencyDiagnostic> diagnosticsBuilder
    ) {

        for (T dependent : dependentContainer.values()) {
            K key = dependent.dependencyKey();
            if (state.remembers(key)) {
                diagnosticsBuilder.append(new DependencyDiagnostic.Duplicate<>(key));
            }
        }

    }

    private void mergeGraph(
            DependentContainer<K, T> dependentContainer,
            DiagnosticsBuilder<DependencyDiagnostic> diagnosticsBuilder
    ) {

        for (T dependent : dependentContainer.values()) {

            K dependentKey = dependent.dependencyKey();
            state.graph().addNode(dependentKey);

            for (DependencyRequest<K> request : dependent.getDependencies()) {

                K dependencyKey = request.key();
                T dependency = dependentContainer.get(dependencyKey);
                DependencyLink link = request.link();

                if (dependency == null) {
                    dependency = state.get(dependencyKey);
                    if (dependency == null) {
                        if (link == DependencyLink.REQUIRED) {
                            diagnosticsBuilder.append(new DependencyDiagnostic.Missing<>(dependentKey, dependencyKey));
                        }
                        continue;
                    }
                }

                Directed<K> edge = switch (request.order()) {
                    case AFTER -> EndpointPairs.directed(dependentKey, dependencyKey);
                    case BEFORE -> EndpointPairs.directed(dependencyKey, dependentKey);
                };

                state.graph().addEdge(edge);

            }

        }

    }

    private void rollbackGraphMerge(
            DependentContainer<K, T> dependentContainer
    ) {
        for (T dependent : dependentContainer.values()) {
            K dependentKey = dependent.dependencyKey();
            state.graph().removeNode(dependentKey);

        }
    }

    @Override
    public synchronized DiagnosticResult<ResolutionResult<K, T>, DependencyDiagnostic> resolveAndRemember(
            @NonNull DependentContainer<K, T> dependentContainer
    ) {

        if (dependentContainer.isEmpty()) return DiagnosticResult.success(ResolutionResult.empty());

        DiagnosticsBuilder<DependencyDiagnostic> diagnosticsBuilder = ListDiagnostics.builder();

        validateDuplicates(dependentContainer, diagnosticsBuilder);
        if (diagnosticsBuilder.has(DiagnosticType.ERROR)) {
            return DiagnosticResult.failure(diagnosticsBuilder.build());
        }

        mergeGraph(dependentContainer, diagnosticsBuilder);
        if (diagnosticsBuilder.has(DiagnosticType.ERROR)) {
            rollbackGraphMerge(dependentContainer);
            return DiagnosticResult.failure(diagnosticsBuilder.build());
        }

        try {
            TopologicalSortResult<K> sortedKeys = sorter.sort(state.graph());
            List<List<T>> layers = sortedKeys.layers().stream()
                    .map(layer -> layer.stream()
                            .filter(dependentContainer::contains)
                            .map(dependentContainer::get)
                            .toList()
                    )
                    .filter(layer -> !layer.isEmpty())
                    .toList();

            List<T> ordered = layers.stream().flatMap(Collection::stream).toList();
            ResolutionResult<K, T> resolutionResult = new ResolutionResult<>(ordered, layers);

            dependentContainer.values().forEach(state::commit);

            return DiagnosticResult.success(resolutionResult);

        } catch (CircularDependencyException ex) {
            CyclePath<K> cycle = ex.getCyclePath();
            diagnosticsBuilder.append(new DependencyDiagnostic.Circular<>(cycle));
            rollbackGraphMerge(dependentContainer);
            return DiagnosticResult.failure(diagnosticsBuilder.build());

        }

    }

}
