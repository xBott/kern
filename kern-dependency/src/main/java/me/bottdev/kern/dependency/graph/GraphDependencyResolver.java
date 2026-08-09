package me.bottdev.kern.dependency.graph;

import lombok.RequiredArgsConstructor;
import me.bottdev.kern.dependency.*;
import me.bottdev.kern.dependency.versioned.VersionedDependencyAware;
import me.bottdev.kern.dependency.versioned.VersionedDependencyRequest;
import me.bottdev.kern.dependency.versioned.VersionedDependencyResolver;
import me.bottdev.kern.struct.algorithms.sort.CircularDependencyException;
import me.bottdev.kern.struct.algorithms.sort.TopologicalSortResult;
import me.bottdev.kern.struct.algorithms.sort.TopologicalSorter;
import me.bottdev.kern.struct.graph.EndpointPairs;
import me.bottdev.kern.struct.graph.Graph;
import me.bottdev.kern.struct.graph.adjacency.AdjacencyListGraphBuilder;
import me.bottdev.kern.struct.graph.endpoints.Directed;
import me.bottdev.kern.version.VersionRange;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/// Implementation of [DependencyResolver] and [VersionedDependencyResolver] that uses a graph and Topological Sort.
@RequiredArgsConstructor
public class GraphDependencyResolver implements DependencyResolver, VersionedDependencyResolver {

    private final TopologicalSorter sorter;

    private <K, T extends DependencyAware<K>> Map<K, T> buildMap(DependentContainer<K, T> dependentContainer) {
        Map<K, T> dependentMap = new HashMap<>();
        dependentContainer.dependents().forEach(dependent ->
                dependentMap.put(dependent.dependencyKey(), dependent)
        );

        return dependentMap;
    }

    /// Builds the graph, skipping edges for missing dependencies rather than throwing.
    /// Any missing dependency is appended to `diagnostics` instead.
    private <K, T extends DependencyAware<K>> Graph<K, Directed<K>> buildGraph(
            DependentContainer<K, T> dependentContainer,
            Map<K, T> dependentMap,
            List<DependencyDiagnostic<K>> diagnostics
    ) {

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
                    diagnostics.add(new DependencyDiagnostic.Missing<>(dependentKey, dependencyKey));
                    continue;
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

    /// Same as [#buildGraph], plus version-mismatch checks against each dependency's
    /// actual resolved version. Mismatched edges are skipped and reported, not thrown.
    private <K, T extends VersionedDependencyAware<K>> Graph<K, Directed<K>> buildVersionedGraph(
            DependentContainer<K, T> dependentContainer,
            Map<K, T> dependentMap,
            List<DependencyDiagnostic<K>> diagnostics
    ) {

        AdjacencyListGraphBuilder<K, Directed<K>> builder = new AdjacencyListGraphBuilder<>();

        for (T dependent : dependentContainer.dependents()) {

            K dependentKey = dependent.dependencyKey();
            builder.addNode(dependentKey);

            for (VersionedDependencyRequest<K> request : dependent.getVersionedDependencies()) {

                DependencyLink link = request.link();
                K dependencyKey = request.key();
                T dependency = dependentMap.get(dependencyKey);

                if (dependency == null) {
                    if (link == DependencyLink.OPTIONAL) continue;
                    diagnostics.add(new DependencyDiagnostic.Missing<>(dependentKey, dependencyKey));
                    continue;
                }

                VersionRange versionRange = request.versionRange();
                if (versionRange != null && !versionRange.satisfies(dependency.getVersion())) {
                    diagnostics.add(new DependencyDiagnostic.VersionMismatch<>(
                            dependentKey,
                            dependencyKey,
                            versionRange,
                            dependency.getVersion()
                    ));
                    continue;
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

    /// Detects requests on the same dependency key whose version ranges don't overlap,
    /// regardless of what version is actually resolved. E.g. A requires foo>=2.0 while
    /// B requires foo<1.0 — that's a conflict even before checking any real foo version.
    private <K, T extends VersionedDependencyAware<K>> List<DependencyDiagnostic<K>> detectVersionConflicts(
            DependentContainer<K, T> dependentContainer
    ) {

        Map<K, List<DependencyDiagnostic.VersionConflict.Entry<K>>> byDependency = new HashMap<>();

        for (T dependent : dependentContainer.dependents()) {
            for (VersionedDependencyRequest<K> request : dependent.getVersionedDependencies()) {

                VersionRange range = request.versionRange();
                if (range == null) continue;

                byDependency
                        .computeIfAbsent(request.key(), k -> new ArrayList<>())
                        .add(new DependencyDiagnostic.VersionConflict.Entry<>(dependent.dependencyKey(), range));
            }
        }

        List<DependencyDiagnostic<K>> conflicts = new ArrayList<>();

        for (Map.Entry<K, List<DependencyDiagnostic.VersionConflict.Entry<K>>> e : byDependency.entrySet()) {
            List<DependencyDiagnostic.VersionConflict.Entry<K>> entries = e.getValue();
            if (entries.size() < 2) continue;

            VersionRange intersection = entries.getFirst().range();
            for (int i = 1; i < entries.size() && intersection != null; i++) {
                intersection = intersection.intersect(entries.get(i).range());
            }

            if (intersection == null || intersection.isEmpty()) {
                conflicts.add(new DependencyDiagnostic.VersionConflict<>(e.getKey(), entries));
            }
        }

        return conflicts;
    }

    /// Attempts the topological sort, converting a thrown [CircularDependencyException]
    /// into a [DependencyDiagnostic.Circular] instead of propagating it.
    private <K, T extends DependencyAware<K>> ResolutionResult<K, T> sortAndConvert(
            Graph<K, Directed<K>> graph,
            Map<K, T> dependentMap
    ) {
        try {
            TopologicalSortResult<K> sortedKeys = sorter.sort(graph);

            List<List<T>> layers = sortedKeys.layers().stream()
                    .map(layer -> layer.stream().map(dependentMap::get).toList())
                    .toList();

            List<T> ordered = layers.stream().flatMap(Collection::stream).toList();

            return new ResolutionResult.Success<>(ordered, layers);

        } catch (CircularDependencyException ex) {
            List<K> cycle = ex.<K>getCycleResult().nodes();
            return new ResolutionResult.Failure<>(List.of(new DependencyDiagnostic.Circular<>(cycle)));

        }

    }

    @Override
    public <K, T extends DependencyAware<K>> ResolutionResult<K, T> resolve(
            DependentContainer<K, T> dependentContainer
    ) {

        Map<K, T> dependentMap = buildMap(dependentContainer);
        List<DependencyDiagnostic<K>> diagnostics = new ArrayList<>();
        Graph<K, Directed<K>> graph = buildGraph(dependentContainer, dependentMap, diagnostics);

        if (!diagnostics.isEmpty()) {
            return new ResolutionResult.Failure<>(diagnostics);
        }

        return sortAndConvert(graph, dependentMap);
    }

    @Override
    public <K, T extends VersionedDependencyAware<K>> ResolutionResult<K, T> resolveVersioned(
            DependentContainer<K, T> dependentContainer
    ) {

        Map<K, T> dependentMap = buildMap(dependentContainer);
        List<DependencyDiagnostic<K>> diagnostics = new ArrayList<>(detectVersionConflicts(dependentContainer));
        Graph<K, Directed<K>> graph = buildVersionedGraph(dependentContainer, dependentMap, diagnostics);

        if (!diagnostics.isEmpty()) {
            return new ResolutionResult.Failure<>(diagnostics);
        }

        return sortAndConvert(graph, dependentMap);
    }

}