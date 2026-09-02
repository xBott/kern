package me.bottdev.kern.dependency.versioned.graph;

import lombok.NonNull;
import me.bottdev.kern.commons.diagnostic.DiagnosticSeverity;
import me.bottdev.kern.commons.diagnostic.DiagnosticsBuilder;
import me.bottdev.kern.commons.diagnostic.ListDiagnostics;
import me.bottdev.kern.commons.wrapper.DiagnosticResult;
import me.bottdev.kern.dependency.*;
import me.bottdev.kern.dependency.versioned.VersionedDependencyAware;
import me.bottdev.kern.dependency.versioned.VersionedDependencyRequest;
import me.bottdev.kern.struct.algorithms.sort.CircularDependencyException;
import me.bottdev.kern.struct.algorithms.sort.TopologicalSortResult;
import me.bottdev.kern.struct.algorithms.sort.TopologicalSorter;
import me.bottdev.kern.struct.graph.EndpointPairs;
import me.bottdev.kern.struct.graph.endpoints.Directed;
import me.bottdev.kern.struct.paths.CyclePath;
import org.semver4j.Semver;

import me.bottdev.kern.dependency.versioned.StatefulVersionedDependencyResolver;

import java.util.*;

/// Implementation of [StatefulVersionedDependencyResolver] for [VersionedDependencyAware] that uses a graph and Topological Sort.
public class GraphStatefulVersionedDependencyResolver<K, T extends VersionedDependencyAware<K>>
        implements StatefulVersionedDependencyResolver<K, T>
{

    private final TopologicalSorter sorter;
    private final GraphDependencyResolverState<K, T> state;

    public GraphStatefulVersionedDependencyResolver(@NonNull TopologicalSorter sorter) {
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

    /// Checks the requested version range against the actual version of the found
    /// dependency—regardless of whether it is from the current batch or has already been committed.
    /// Returns true if the version is compatible (or if the check does not apply -
    /// the request is not version-specific), false if a conflict is found (diagnostics have already been added).
    private boolean validateVersion(
            K dependentKey,
            K dependencyKey,
            T dependency,
            VersionedDependencyRequest<K> request,
            DiagnosticsBuilder<DependencyDiagnostic> diagnosticsBuilder
    ) {

        String versionRange = request.versionRange();
        String actualVersion = dependency.version();

        Semver semver = new Semver(actualVersion);
        if (semver.satisfies(versionRange)) {
            return true;
        }

        diagnosticsBuilder.append(
                new DependencyDiagnostic.VersionMismatch<>(dependentKey, dependencyKey, versionRange, actualVersion)
        );

        return false;
    }



    private void mergeGraph(
            DependentContainer<K, T> dependentContainer,
            DiagnosticsBuilder<DependencyDiagnostic> diagnosticsBuilder
    ) {

        for (T dependent : dependentContainer.values()) {
            state.graph().addNode(dependent.dependencyKey());
        }

        // Add edges from new nodes
        for (T dependent : dependentContainer.values()) {
            K dependentKey = dependent.dependencyKey();
            for (VersionedDependencyRequest<K> request : dependent.getVersionedDependencies()) {
                addRequestEdge(dependentKey, request, dependentContainer, diagnosticsBuilder);
            }
        }
        
        // Add edges from existing nodes to new nodes
        for (T dependent : state.committed()) {
            K dependentKey = dependent.dependencyKey();
            for (VersionedDependencyRequest<K> request : dependent.getVersionedDependencies()) {
                if (dependentContainer.contains(request.key())) {
                    addRequestEdge(dependentKey, request, dependentContainer, diagnosticsBuilder);
                }
            }
        }
    }

    private void addRequestEdge(
            K dependentKey,
            VersionedDependencyRequest<K> request,
            DependentContainer<K, T> dependentContainer,
            DiagnosticsBuilder<DependencyDiagnostic> diagnosticsBuilder
    ) {
        K dependencyKey = request.key();
        T dependency = dependentContainer.get(dependencyKey);
        DependencyLink link = request.link();

        if (dependency == null) {
            dependency = state.get(dependencyKey);
            if (dependency == null) {
                if (link == DependencyLink.REQUIRED) {
                    diagnosticsBuilder.append(new DependencyDiagnostic.Missing<>(dependentKey, dependencyKey));
                }
                return;
            }
        }

        boolean versionOk = validateVersion(dependentKey, dependencyKey, dependency, request, diagnosticsBuilder);
        if (!versionOk) return;

        Directed<K> edge = switch (request.order()) {
            case AFTER -> EndpointPairs.directed(dependentKey, dependencyKey);
            case BEFORE -> EndpointPairs.directed(dependencyKey, dependentKey);
        };

        state.graph().addEdge(edge);
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
        if (diagnosticsBuilder.has(DiagnosticSeverity.ERROR)) {
            return DiagnosticResult.failure(diagnosticsBuilder.build());
        }

        mergeGraph(dependentContainer, diagnosticsBuilder);
        if (diagnosticsBuilder.has(DiagnosticSeverity.ERROR)) {
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
