package me.bottdev.kern.commons.diagnostic;

import lombok.NonNull;

import java.util.*;
import java.util.stream.Collectors;

public record ListDiagnostics<D extends Diagnostic>(
        List<D> items
) implements Diagnostics<D> {

    public static class Builder<D extends Diagnostic> implements DiagnosticsBuilder<D> {

        private final List<D> buffer = new ArrayList<>();

        @Override
        public boolean isEmpty() {
            return buffer.isEmpty();
        }

        @Override
        public boolean has(DiagnosticSeverity type) {
            return buffer.stream().anyMatch(diagnostic -> diagnostic.severity() == type);
        }

        @Override
        public DiagnosticsBuilder<D> append(D diagnostic) {
            buffer.add(diagnostic);
            return this;
        }

        @Override
        public Diagnostics<D> build() {
            return new ListDiagnostics<>(buffer);
        }

    }

    public static <D extends Diagnostic> Builder<D> builder() {
        return new Builder<>();
    }

    public static <D extends Diagnostic> ListDiagnostics<D> empty() {
        return new ListDiagnostics<>(List.of());
    }


    public ListDiagnostics(List<D> items) {
        this.items = List.copyOf(items);
    }

    @Override
    public int size() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        return items.isEmpty();
    }

    private boolean computeHas(DiagnosticSeverity type) {
        return items.stream()
                .anyMatch(item -> item.severity() == type);
    }

    @Override
    public boolean has(DiagnosticSeverity type) {
        return computeHas(type);
    }

    @Override
    public List<D> ofType(DiagnosticSeverity type) {
        return items.stream().filter(item -> item.severity() == type).toList();
    }

    @Override
    public List<D> all() {
        return List.copyOf(items);
    }

    @Override
    public Map<DiagnosticSeverity, List<D>> grouped() {
        return items.stream().collect(Collectors.groupingBy(Diagnostic::severity));
    }

    @Override
    @NonNull
    public Iterator<D> iterator() {
        return items.iterator();
    }

    @Override
    @NonNull
    public String toString() {
        StringBuilder sb = new StringBuilder("Diagnostics[\n");
        items.forEach(item -> sb.append(item.message()).append("\n"));
        sb.append("]");
        return sb.toString();
    }

}
