package me.bottdev.kern.dependency.graph;

import lombok.NonNull;
import me.bottdev.kern.commons.diagnostic.DiagnosticType;
import me.bottdev.kern.commons.diagnostic.DiagnosticsBuilder;
import me.bottdev.kern.commons.diagnostic.ListDiagnostics;
import me.bottdev.kern.commons.wrapper.DiagnosticResult;
import me.bottdev.kern.dependency.*;
import me.bottdev.kern.dependency.versioned.VersionConflictDetector;
import me.bottdev.kern.dependency.versioned.VersionedDependencyAware;
import me.bottdev.kern.dependency.versioned.VersionedDependencyRequest;
import me.bottdev.kern.struct.algorithms.sort.CircularDependencyException;
import me.bottdev.kern.struct.algorithms.sort.TopologicalSortResult;
import me.bottdev.kern.struct.algorithms.sort.TopologicalSorter;
import me.bottdev.kern.struct.graph.EndpointPairs;
import me.bottdev.kern.struct.graph.endpoints.Directed;
import me.bottdev.kern.struct.paths.CyclePath;
import me.bottdev.kern.version.SemVersion;
import me.bottdev.kern.version.VersionRange;

import java.util.*;

/// Implementation of [StatefulDependencyResolver] for [VersionedDependencyAware] that uses a graph and Topological Sort.
public class GraphStatefulVersionedDependencyResolver<K, T extends VersionedDependencyAware<K>>
        implements StatefulDependencyResolver<K, T>
{

    private final TopologicalSorter sorter;
    private final GraphDependencyResolverState<K, T> state;

    public GraphStatefulVersionedDependencyResolver(@NonNull TopologicalSorter sorter) {
        this.sorter = sorter;
        this.state = new GraphDependencyResolverState<>();
    }

    @Override
    public DependencyResolverState<K, T> state() {
        return state;
    }

    private void validateDuplicates(
            DependentContainer<K, T> dependentContainer,
            DiagnosticsBuilder<DependencyDiagnostic> diagnosticsBuilder
    ) {

        for (T dependent : dependentContainer.values()) {
            K key = dependent.dependencyKey();
            if (state.remembers(key)) {
                diagnosticsBuilder.append(DependencyDiagnostic.duplicate(key));
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

        VersionRange versionRange = request.versionRange();
        SemVersion actualVersion = dependency.version();

        if (versionRange.satisfies(actualVersion)) {
            return true;
        }

        diagnosticsBuilder.append(
                DependencyDiagnostic.versionMismatch(dependentKey, dependencyKey, versionRange, actualVersion)
        );

        return false;
    }



    private void mergeGraph(
            DependentContainer<K, T> dependentContainer,
            DiagnosticsBuilder<DependencyDiagnostic> diagnosticsBuilder
    ) {

        for (T dependent : dependentContainer.values()) {

            K dependentKey = dependent.dependencyKey();
            state.graph().addNode(dependentKey);

            for (VersionedDependencyRequest<K> request : dependent.getVersionedDependencies()) {

                K dependencyKey = request.key();
                T dependency = dependentContainer.get(dependencyKey);
                DependencyLink link = request.link();

                if (dependency == null) {
                    dependency = state.get(dependencyKey);
                    if (dependency == null) {
                        if (link == DependencyLink.REQUIRED) {
                            diagnosticsBuilder.append(DependencyDiagnostic.missing(dependentKey, dependencyKey));
                        }
                        continue;
                    }
                }

                boolean versionOk = validateVersion(dependentKey, dependencyKey, dependency, request, diagnosticsBuilder);
                if (!versionOk) continue;

                Directed<K> edge = switch (request.order()) {
                    case BEFORE -> EndpointPairs.directed(dependentKey, dependencyKey);
                    case AFTER -> EndpointPairs.directed(dependencyKey, dependentKey);
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
            DependentContainer<K, T> dependentContainer
    ) {

        if (dependentContainer.isEmpty()) return DiagnosticResult.success(ResolutionResult.empty());

        DiagnosticsBuilder<DependencyDiagnostic> diagnosticsBuilder = ListDiagnostics.builder();

        validateDuplicates(dependentContainer, diagnosticsBuilder);
        if (diagnosticsBuilder.has(DiagnosticType.ERROR)) {
            return DiagnosticResult.failure(diagnosticsBuilder.build());
        }

        List<T> completeDependents = new ArrayList<>(dependentContainer.values());
        completeDependents.addAll(state.committed());
        VersionConflictDetector.detect(completeDependents, diagnosticsBuilder);
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
            diagnosticsBuilder.append(DependencyDiagnostic.circular(cycle));
            rollbackGraphMerge(dependentContainer);
            return DiagnosticResult.failure(diagnosticsBuilder.build());

        }

    }

}
