package me.bottdev.kern.commons.diagnostic;

import me.bottdev.kern.commons.diagnostic.exceptions.DiagnosticException;

import java.util.List;
import java.util.Map;

/// Interface of a set of diagnostics.
public interface Diagnostics<D extends Diagnostic> extends Iterable<D> {

    /// @return Amount of diagnostics.
    int size();

    /// @return Indicates whether the set is empty.
    boolean isEmpty();

    /// @return Indicates whether the set contains any diagnostics of a specified type.
    boolean has(DiagnosticType type);

    /// @return Indicates whether the set contains any errors.
    default boolean hasErrors() {
        return has(DiagnosticType.ERROR);
    }

    /// @return Indicates whether the set contains any warnings.
    default boolean hasWarnings() {
        return has(DiagnosticType.WARN);
    }

    /// @return A list of all diagnostics.
    List<D> all();

    /// @return A list of diagnostics with a specified type.
    List<D> ofType(DiagnosticType type);

    /// @return A snapshot of diagnostics grouped by theirs type.
    Map<DiagnosticType, List<D>> grouped();

    /// @throws DiagnosticException if there are any errors present.
    default void throwIfHasErrors() throws DiagnosticException {
        if (hasErrors()) throw new DiagnosticException(this);
    }

}
