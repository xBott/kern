package me.bottdev.kern.dependency;

import me.bottdev.kern.commons.diagnostic.Diagnostic;
import me.bottdev.kern.commons.diagnostic.DiagnosticType;
import me.bottdev.kern.struct.paths.CyclePath;
import me.bottdev.kern.version.SemVersion;
import me.bottdev.kern.version.VersionRange;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.stream.Collectors;

/// A record that describes an error occurred in dependency resolution process.
public sealed interface DependencyDiagnostic extends Diagnostic permits
        DependencyDiagnostic.Missing,
        DependencyDiagnostic.VersionMismatch,
        DependencyDiagnostic.VersionConflict,
        DependencyDiagnostic.Circular
{

    static <K> DependencyDiagnostic missing(
            @NonNull K dependent,
            @NonNull K missingKey
    ) {
        return new Missing<>(dependent, missingKey);
    }

    static <K> DependencyDiagnostic versionMismatch(
            @NonNull K dependent,
            @NonNull K dependencyKey,
            @NonNull VersionRange required,
            @NonNull SemVersion actual
    ) {
        return new VersionMismatch<>(dependent, dependencyKey, required, actual);
    }

    static <K> DependencyDiagnostic versionConflict(
            @NonNull K dependencyKey,
            @NonNull List<VersionConflict.Entry<K>> entries
    ) {
        return new VersionConflict<>(dependencyKey, entries);
    }

    static <K> DependencyDiagnostic circular(
            @NonNull CyclePath<K> cycle
    ) {
        return new Circular<>(cycle);
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
            K dependent,
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
            return "Version mismatch: '" + dependent + "' requires '" + dependencyKey +
                    "' in range " + required + ", but found " + actual;
        }
    }

    record VersionConflict<K>(
            K dependencyKey,
            List<Entry<K>> entries
    ) implements DependencyDiagnostic {

        public record Entry<K>(K requesterKey, VersionRange range) {}

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

}
