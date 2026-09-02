package me.bottdev.kern.commons.diagnostic;

import lombok.NonNull;

import java.util.function.Consumer;

@FunctionalInterface
public interface DiagnosticSink<D extends Diagnostic> {

    void accept(D diagnostic);

    /// Returns a composed sink that performs, in sequence, this operation followed by the `after` operation.
    default DiagnosticSink<D> andThen(@NonNull DiagnosticSink<? super D> after) {
        return (D d) -> { accept(d); after.accept(d); };
    }

    /// A sink that ignores all incoming diagnostics.
    static <D extends Diagnostic> DiagnosticSink<D> noOp() {
        return _ -> {};
    }

    /// A sink that forwards diagnostics to a consumer (e.g., a logger).
    static <D extends Diagnostic> DiagnosticSink<D> forwarding(@NonNull Consumer<D> consumer) {
        return consumer::accept;
    }

}
