package me.bottdev.kern.dependency.versioned;

import lombok.NonNull;
import me.bottdev.kern.commons.diagnostic.DiagnosticsBuilder;
import me.bottdev.kern.dependency.DependencyDiagnostic;
import me.bottdev.kern.version.VersionRange;

import java.util.*;

public class VersionConflictDetector {

    /// Detects requests on the same dependency key whose version ranges don't overlap,
    /// regardless of what version is actually resolved. E.g. A requires foo>=2.0 while
    /// B requires foo<1.0 — that's a conflict even before checking any real foo version.
    ///
    /// Only considers requests within the CURRENT batch (dependentContainer). Conflicts
    /// against already-committed versions are caught later, during mergeGraph's
    /// per-candidate version validation — this method intentionally does not look at
    /// resolver state, since it runs before any candidate is resolved.
    public static <K, T extends VersionedDependencyAware<K>> void detect(
            @NonNull Collection<T> dependents,
            @NonNull DiagnosticsBuilder<DependencyDiagnostic> diagnosticsBuilder
    ) {

        Map<K, List<VersionConflictEntry<K>>> byDependency = new HashMap<>();

        for (T dependent : dependents) {
            for (VersionedDependencyRequest<K> request : dependent.getVersionedDependencies()) {

                VersionRange range = request.versionRange();
                if (range.isEmpty()) continue;

                byDependency
                        .computeIfAbsent(request.key(), _ -> new ArrayList<>())
                        .add(new VersionConflictEntry<>(dependent.dependencyKey(), range));
            }
        }

        for (Map.Entry<K, List<VersionConflictEntry<K>>> entry : byDependency.entrySet()) {

            List<VersionConflictEntry<K>> entries = entry.getValue();
            if (entries.size() < 2) continue;

            VersionRange intersection = intersectAll(entries);
            if (!intersection.isEmpty()) continue;

            VersionConflictingPair<K> pair = findMinimalConflictingPair(entries);

            diagnosticsBuilder.append(DependencyDiagnostic.versionConflict(
                    entry.getKey(),
                    pair != null ? List.of(pair.first(), pair.second()) : entries
            ));
        }

    }

    private static <K> VersionRange intersectAll(List<VersionConflictEntry<K>> entries) {
        VersionRange intersection = entries.getFirst().range();
        for (int i = 1; i < entries.size() && !intersection.isEmpty(); i++) {
            VersionRange next = entries.get(i).range();
            intersection = intersection.intersect(next);
        }
        return intersection;
    }

    private static <K> VersionConflictingPair<K> findMinimalConflictingPair(List<VersionConflictEntry<K>> entries) {

        for (int i = 0; i < entries.size(); i++) {
            for (int j = i + 1; j < entries.size(); j++) {
                VersionRange pairwise = entries.get(i).range().intersect(entries.get(j).range());
                if (pairwise.isEmpty()) {
                    return new VersionConflictingPair<>(entries.get(i), entries.get(j));
                }
            }
        }

        VersionRange running = entries.getFirst().range();
        for (int i = 1; i < entries.size(); i++) {
            VersionRange next = running.intersect(entries.get(i).range());
            if (next.isEmpty()) {
                return new VersionConflictingPair<>(entries.getFirst(), entries.get(i));
            }
            running = next;
        }

        return null;
    }



}
