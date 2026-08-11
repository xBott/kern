package me.bottdev.kern.commons.diagnostic;

import lombok.NonNull;

import java.util.*;
import java.util.stream.Collectors;

public class ListDiagnostics implements Diagnostics {

    public static class Builder implements DiagnosticsBuilder {

        private final List<Diagnostic> buffer = new ArrayList<>();

        @Override
        public boolean isEmpty() {
            return buffer.isEmpty();
        }

        @Override
        public boolean has(DiagnosticType type) {
            return buffer.stream().anyMatch(diagnostic -> diagnostic.type() == type);
        }

        @Override
        public DiagnosticsBuilder append(Diagnostic diagnostic) {
            buffer.add(diagnostic);
            return this;
        }

        @Override
        public Diagnostics build() {
            return new ListDiagnostics(buffer);
        }

    }

    public static Builder builder() {
        return new Builder();
    }

    private final List<Diagnostic> items;

    public ListDiagnostics(List<Diagnostic> items) {
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

    private boolean computeHas(DiagnosticType type) {
        return items.stream()
                .anyMatch(item -> item.type() == type);
    }

    @Override
    public boolean has(DiagnosticType type) {
        return computeHas(type);
    }

    @Override
    public List<Diagnostic> ofType(DiagnosticType type) {
        return items.stream().filter(item -> item.type() == type).toList();
    }

    @Override
    public List<Diagnostic> all() {
        return List.copyOf(items);
    }

    @Override
    public Map<DiagnosticType, List<Diagnostic>> grouped() {
        return items.stream().collect(Collectors.groupingBy(Diagnostic::type));
    }

    @Override
    @NonNull
    public Iterator<Diagnostic> iterator() {
        return items.iterator();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Diagnostics[\n");
        items.forEach(item -> sb.append(item.message()).append("\n"));
        sb.append("]");
        return sb.toString();
    }
}
