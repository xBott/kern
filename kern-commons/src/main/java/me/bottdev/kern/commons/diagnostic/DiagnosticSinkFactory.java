package me.bottdev.kern.commons.diagnostic;

import lombok.NonNull;

/// Interface used for instantiating [DiagnosticSink]
@FunctionalInterface
public interface DiagnosticSinkFactory {

    @NonNull
    <D extends Diagnostic> DiagnosticSink<D> create();

}
