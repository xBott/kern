package me.bottdev.kern.meta.core.diagnostic;

import java.util.ArrayList;
import java.util.List;

public final class DiagnosticBuilder<R> {

    private final List<DiagnosticRule<R>> rules = new ArrayList<>();

    public DiagnosticBuilder<R> rule(DiagnosticRule<R> rule) {
        rules.add(rule);
        return this;
    }

    public Diagnostic<R> build() {
        return new Diagnostic<>(rules);
    }

}