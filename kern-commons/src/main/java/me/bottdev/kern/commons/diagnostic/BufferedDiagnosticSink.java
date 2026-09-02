package me.bottdev.kern.commons.diagnostic;

import me.bottdev.kern.commons.buffer.ConcurrentRingBuffer;
import me.bottdev.kern.commons.buffer.RingBuffer;

import java.util.List;
import java.util.function.Consumer;

/// A [DiagnosticSink] that accumulates diagnostics in memory.
/// Allows inspecting the current state (e.g., checking for errors) during accumulation.
public class BufferedDiagnosticSink<D extends Diagnostic> implements DiagnosticSink<D> {

    private final RingBuffer<D> buffer;
    private final Consumer<D> consumer;

    public BufferedDiagnosticSink(int capacity, Consumer<D> consumer) {
        this.buffer = new ConcurrentRingBuffer<>(capacity);
        this.consumer = consumer;
    }

    public BufferedDiagnosticSink(int capacity) {
        this.buffer = new ConcurrentRingBuffer<>(capacity);
        this.consumer = null;
    }

    @Override
    public void accept(D diagnostic) {
        buffer.add(diagnostic);
        if (consumer != null) consumer.accept(diagnostic);
    }

    /// @return An unmodifiable view of the currently accumulated diagnostics.
    public Diagnostics<D> getDiagnostics() {
        return new ListDiagnostics<>(buffer.snapshot());
    }

    /// @return true if there is at least one diagnostic of the specified type.
    public boolean has(DiagnosticSeverity type) {
        return buffer.stream().anyMatch(d -> d.severity() == type);
    }
    
    /// @return true if there is at least one error diagnostic.
    public boolean hasErrors() {
        return has(DiagnosticSeverity.ERROR);
    }

    /// Clears the accumulated diagnostics.
    public void clear() {
        buffer.clear();
    }

}
