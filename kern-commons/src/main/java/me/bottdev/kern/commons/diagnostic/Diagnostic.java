package me.bottdev.kern.commons.diagnostic;

/// Interface for a diagnostic record.
/// Represents a single statement.
public interface Diagnostic {

    DiagnosticType type();

    /// Human-readable description of this diagnostic. Implemented per-variant instead
    /// of centrally, so adding a new Diagnostic subtype can't forget to describe it.
    String message();

}
