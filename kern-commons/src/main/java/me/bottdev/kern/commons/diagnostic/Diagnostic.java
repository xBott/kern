package me.bottdev.kern.commons.diagnostic;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/// Interface for a diagnostic record.
/// Represents a single statement.
public interface Diagnostic {

    DiagnosticSeverity severity();

    /// The unique identifier/type of this diagnostic (e.g., "missing_dependency").
    String type();

    /// Human-readable description of this diagnostic. Implemented per-variant instead
    /// of centrally, so adding a new Diagnostic subtype can't forget to describe it.
    String message();

    /// Returns additional, specific structured data for logging.
    /// Override this method instead of `metadata()` to add custom fields.
    default Map<String, Object> details() {
        return Map.of();
    }

    /// Returns the complete structured data for logging, including the base fields
    /// and any custom fields provided by `details()`.
    default Map<String, Object> metadata() {
        Map<String, Object> map = new HashMap<>();
        map.put("diagnostic_type", type());
        map.put("diagnostic_severity", severity().name());
        map.putAll(details());
        return Collections.unmodifiableMap(map);
    }

}
