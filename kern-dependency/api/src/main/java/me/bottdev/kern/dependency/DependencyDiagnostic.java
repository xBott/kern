package me.bottdev.kern.dependency;

import lombok.NonNull;
import me.bottdev.kern.commons.diagnostic.Diagnostic;
import me.bottdev.kern.commons.diagnostic.DiagnosticSeverity;
import me.bottdev.kern.struct.paths.CyclePath;

import java.util.Map;

/// A record that describes an error occurred in dependency resolution process.
public sealed interface DependencyDiagnostic extends Diagnostic permits
        DependencyDiagnostic.Missing,
        DependencyDiagnostic.VersionMismatch,
        DependencyDiagnostic.Circular,
        DependencyDiagnostic.Duplicate
{

    record Missing<K>(
            @NonNull K dependent,
            @NonNull K missingKey
    ) implements DependencyDiagnostic {

        @Override
        public DiagnosticSeverity severity() {
            return DiagnosticSeverity.ERROR;
        }

        @Override
        public String type() {
            return "missing_dependency";
        }

        @Override
        public String message() {
            return "Missing dependency: '" + dependent + "' requires '" + missingKey + "', which does not exist";
        }

        @Override
        public Map<String, Object> details() {
            return Map.of(
                    "dependent_id", dependent,
                    "missing_id", missingKey
            );
        }
    }

    record VersionMismatch<K>(
            @NonNull K dependentKey,
            @NonNull K dependencyKey,
            @NonNull String required,
            @NonNull String actual
    ) implements DependencyDiagnostic {

        @Override
        public DiagnosticSeverity severity() {
            return DiagnosticSeverity.ERROR;
        }

        @Override
        public String type() {
            return "version_mismatch";
        }

        @Override
        public String message() {
            return "Version mismatch: '" + dependentKey + "' requires '" + dependencyKey +
                    "' in range " + required + ", but found " + actual;
        }

        @Override
        public Map<String, Object> details() {
            return Map.of(
                    "dependent_id", dependentKey,
                    "dependency_id", dependencyKey,
                    "required_version", required,
                    "actual_version", actual
            );
        }
    }

    record Circular<K>(@NonNull CyclePath<K> cycle) implements DependencyDiagnostic {

        @Override
        public DiagnosticSeverity severity() {
            return DiagnosticSeverity.ERROR;
        }

        @Override
        public String type() {
            return "circular_dependency";
        }

        @Override
        public String message() {
            return "Circular dependency: " + cycle.toString();
        }

        @Override
        public Map<String, Object> details() {
            return Map.of(
                    "cycle_path", cycle.toString()
            );
        }

    }

    record Duplicate<K>(@NonNull K dependencyKey) implements DependencyDiagnostic {

        @Override
        public DiagnosticSeverity severity() {
            return DiagnosticSeverity.ERROR;
        }

        @Override
        public String type() {
            return "duplicate_entry";
        }

        @Override
        public String message() {
            return "Found duplicate entry: " + dependencyKey;
        }

        @Override
        public Map<String, Object> details() {
            return Map.of(
                    "dependency_id", dependencyKey
            );
        }
    }

}
