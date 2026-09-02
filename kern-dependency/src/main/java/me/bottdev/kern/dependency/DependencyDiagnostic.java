package me.bottdev.kern.dependency;

import me.bottdev.kern.commons.diagnostic.Diagnostic;
import me.bottdev.kern.commons.diagnostic.DiagnosticType;
import me.bottdev.kern.dependency.versioned.VersionConflictEntry;
import me.bottdev.kern.struct.paths.CyclePath;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.stream.Collectors;

/// A record that describes an error occurred in dependency resolution process.
public sealed interface DependencyDiagnostic extends Diagnostic permits
        DependencyDiagnostic.Missing,
        DependencyDiagnostic.VersionMismatch,
        DependencyDiagnostic.VersionConflict,
        DependencyDiagnostic.Circular,
        DependencyDiagnostic.Duplicate
{

    static <K> DependencyDiagnostic missing(
            @NonNull K dependent,
            @NonNull K missingKey
    ) {
        return new Missing<>(dependent, missingKey);
    }

    static <K> DependencyDiagnostic versionMismatch(
            @NonNull K dependentKey,
            @NonNull K dependencyKey,
            @NonNull VersionRange required,
            @NonNull SemVersion actual
    ) {
        return new VersionMismatch<>(dependentKey, dependencyKey, required, actual);
    }

    static <K> DependencyDiagnostic versionConflict(
            @NonNull K dependencyKey,
            @NonNull List<VersionConflictEntry<K>> entries
    ) {
        return new VersionConflict<>(dependencyKey, entries);
    }

    static <K> DependencyDiagnostic circular(
            @NonNull CyclePath<K> cycle
    ) {
        return new Circular<>(cycle);
    }

    static <K> DependencyDiagnostic duplicate(
            @NonNull K dependencyKey
    ) {
        return new Duplicate<>(dependencyKey);
    }

    record Missing<K>(K dependent, K missingKey) implements DependencyDiagnostic {

        @Override
        public DiagnosticType type() {
            return DiagnosticType.ERROR;
        }

        @Override
        public String message() {
            return "Missing dependency: '" + dependent + "' requires '" + missingKey + "', which does not exist";
        }
    }

    record VersionMismatch<K>(
            K dependentKey,
            K dependencyKey,
            VersionRange required,
            SemVersion actual
    ) implements DependencyDiagnostic {

        @Override
        public DiagnosticType type() {
            return DiagnosticType.ERROR;
        }

        @Override
        public String message() {
            return "Version mismatch: '" + dependentKey + "' requires '" + dependencyKey +
                    "' in range " + required + ", but found " + actual;
        }
    }

    record VersionConflict<K>(
            K dependencyKey,
            List<VersionConflictEntry<K>> entries
    ) implements DependencyDiagnostic {

        @Override
        public DiagnosticType type() {
            return DiagnosticType.ERROR;
        }

        @Override
        public String message() {
            return "Version conflict on '" + dependencyKey + "': " +
                    entries.stream()
                            .map(e -> e.requesterKey() + " requires " + e.range())
                            .collect(Collectors.joining("; "));
        }

    }

    record Circular<K>(CyclePath<K> cycle) implements DependencyDiagnostic {

        @Override
        public DiagnosticType type() {
            return DiagnosticType.ERROR;
        }

        @Override
        public String message() {
            return "Circular dependency: " + cycle.toString();
        }

    }

    record Duplicate<K>(K dependencyKey) implements DependencyDiagnostic {

        @Override
        public DiagnosticType type() {
            return DiagnosticType.ERROR;
        }

        @Override
        public String message() {
            return "Found duplicate entry: " + dependencyKey;
        }
    }

}
