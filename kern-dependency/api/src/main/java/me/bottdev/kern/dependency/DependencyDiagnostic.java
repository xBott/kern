package me.bottdev.kern.dependency;

import lombok.NonNull;
import me.bottdev.kern.commons.diagnostic.Diagnostic;
import me.bottdev.kern.commons.diagnostic.DiagnosticType;
import me.bottdev.kern.struct.paths.CyclePath;

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
        public DiagnosticType type() {
            return DiagnosticType.ERROR;
        }

        @Override
        public String message() {
            return "Missing dependency: '" + dependent + "' requires '" + missingKey + "', which does not exist";
        }
    }

    record VersionMismatch<K>(
            @NonNull K dependentKey,
            @NonNull K dependencyKey,
            @NonNull String required,
            @NonNull String actual
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

    record Circular<K>(@NonNull CyclePath<K> cycle) implements DependencyDiagnostic {

        @Override
        public DiagnosticType type() {
            return DiagnosticType.ERROR;
        }

        @Override
        public String message() {
            return "Circular dependency: " + cycle.toString();
        }

    }

    record Duplicate<K>(@NonNull K dependencyKey) implements DependencyDiagnostic {

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
