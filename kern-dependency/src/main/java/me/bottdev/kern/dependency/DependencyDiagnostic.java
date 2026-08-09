package me.bottdev.kern.dependency;

import me.bottdev.kern.version.SemVersion;
import me.bottdev.kern.version.VersionRange;

import java.util.List;
import java.util.stream.Collectors;

/// A record that describes an error occurred in dependency resolution process.
public sealed interface DependencyDiagnostic<K>
        permits DependencyDiagnostic.Missing, DependencyDiagnostic.VersionMismatch, DependencyDiagnostic.VersionConflict, DependencyDiagnostic.Circular {

    /// Human-readable description of this diagnostic. Implemented per-variant instead
    /// of centrally, so adding a new Diagnostic subtype can't forget to describe it.
    String describe();

    record Missing<K>(K dependent, K missingKey) implements DependencyDiagnostic<K> {
        @Override
        public String describe() {
            return "Missing dependency: '" + dependent + "' requires '" + missingKey + "', which does not exist";
        }
    }

    record VersionMismatch<K>(
            K dependent,
            K dependencyKey,
            VersionRange required,
            SemVersion actual
    ) implements DependencyDiagnostic<K> {
        @Override
        public String describe() {
            return "Version mismatch: '" + dependent + "' requires '" + dependencyKey +
                    "' in range " + required + ", but found " + actual;
        }
    }

    record VersionConflict<K>(
            K dependencyKey,
            List<Entry<K>> entries
    ) implements DependencyDiagnostic<K> {

        public record Entry<K>(K requesterKey, VersionRange range) {}

        @Override
        public String describe() {
            return "Version conflict on '" + dependencyKey + "': " +
                    entries.stream()
                            .map(e -> e.requesterKey() + " requires " + e.range())
                            .collect(Collectors.joining("; "));
        }
    }

    record Circular<K>(List<K> cycle) implements DependencyDiagnostic<K> {
        @Override
        public String describe() {
            return "Circular dependency: " + cycle.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(" -> ")) + " ---> " + cycle.getFirst();
        }
    }

}
