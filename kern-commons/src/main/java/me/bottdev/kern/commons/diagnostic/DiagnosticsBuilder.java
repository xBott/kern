package me.bottdev.kern.commons.diagnostic;

/// Interface that allows to create diagnostics in a convenient, human-readable way.
public interface DiagnosticsBuilder<D extends Diagnostic> {

    /// @return Indicates whether the builder is empty.
    boolean isEmpty();

    /// @return Indicates whether the builder contains any diagnostics of a specified type.
    boolean has(DiagnosticType type);

    /// Adds a new diagnostics.
    DiagnosticsBuilder<D> append(D diagnostic);

    /// Builds the diagnostics and returns a read-only record.
    Diagnostics<D> build();


}
