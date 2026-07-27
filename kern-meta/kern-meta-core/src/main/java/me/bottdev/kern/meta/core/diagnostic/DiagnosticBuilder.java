package me.bottdev.kern.meta.core.diagnostic;

public interface DiagnosticBuilder<R extends DiagnosticRule> {

    DiagnosticBuilder<R> rule(R rule);

    DiagnosticResult build();

}
