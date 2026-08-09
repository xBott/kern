package me.bottdev.kern.dependency.exceptions;

import lombok.Getter;
import me.bottdev.kern.dependency.DependencyDiagnostic;

import java.util.List;

/// Unchecked aggregate thrown by [me.bottdev.kern.dependency.ResolutionResult#orElseThrow()]
/// when resolution failed. Carries every diagnostic, not just the first one.
@Getter
public class AggregatedDependencyException extends RuntimeException {

    private final List<? extends DependencyDiagnostic<?>> diagnostics;

    public AggregatedDependencyException(List<? extends DependencyDiagnostic<?>> diagnostics) {
        super(buildMessage(diagnostics));
        this.diagnostics = List.copyOf(diagnostics);
    }

    private static String buildMessage(List<? extends DependencyDiagnostic<?>> diagnostics) {
        if (diagnostics.isEmpty()) {
            return "Dependency resolution failed with no diagnostics.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Dependency resolution failed with ")
                .append(diagnostics.size())
                .append(" issue(s):\n");

        int index = 1;
        for (DependencyDiagnostic<?> diagnostic : diagnostics) {
            sb.append("  ").append(index++).append(") ").append(diagnostic.describe()).append("\n");
        }

        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    public <K> List<DependencyDiagnostic.Missing<K>> missing() {
        return filter(DependencyDiagnostic.Missing.class);
    }

    @SuppressWarnings("unchecked")
    public <K> List<DependencyDiagnostic.VersionMismatch<K>> versionMismatches() {
        return filter(DependencyDiagnostic.VersionMismatch.class);
    }

    @SuppressWarnings("unchecked")
    public <K> List<DependencyDiagnostic.VersionConflict<K>> versionConflicts() {
        return filter(DependencyDiagnostic.VersionConflict.class);
    }

    @SuppressWarnings("unchecked")
    public <K> List<DependencyDiagnostic.Circular<K>> circular() {
        return filter(DependencyDiagnostic.Circular.class);
    }

    @SuppressWarnings("unchecked")
    private <D> List<D> filter(Class<?> type) {
        return diagnostics.stream()
                .filter(type::isInstance)
                .map(d -> (D) d)
                .toList();
    }

}