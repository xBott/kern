package me.bottdev.kern.meta.core.diagnostic.bound;

import lombok.RequiredArgsConstructor;
import me.bottdev.kern.meta.core.diagnostic.DiagnosticBuilder;
import me.bottdev.kern.meta.core.diagnostic.DiagnosticResult;
import me.bottdev.kern.meta.core.diagnostic.DiagnosticRuleResult;
import me.bottdev.kern.meta.core.exceptions.DiagnosticCheckException;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public final class BoundDiagnosticBuilder<M> implements DiagnosticBuilder<BoundDiagnosticRule<M>> {

    private final M model;
    private final List<BoundDiagnosticRule<M>> rules = new ArrayList<>();

    public BoundDiagnosticBuilder<M> rule(BoundDiagnosticRule<M> rule) {
        rules.add(rule);
        return this;
    }

    @Override
    public DiagnosticResult build() {

        List<DiagnosticRuleResult.Error> errors = new ArrayList<>();
        List<DiagnosticRuleResult.Warn> warns = new ArrayList<>();

        for (BoundDiagnosticRule<M> rule : rules) {
            try {

                switch (rule.check(model)) {
                    case DiagnosticRuleResult.Error error -> errors.add(error);
                    case DiagnosticRuleResult.Warn warn -> warns.add(warn);
                    default -> {}
                }

            } catch (Exception ex) {
                throw new DiagnosticCheckException("Failed to check rule", ex);

            }
        }

        return new DiagnosticResult(errors, warns);

    }

}