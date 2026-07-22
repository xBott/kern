package me.bottdev.kern.meta.core.diagnostic;

import me.bottdev.kern.meta.core.exceptions.DiagnosticCheckException;

import java.util.ArrayList;
import java.util.List;

public record Diagnostic<R>(List<DiagnosticRule<R>> rules) {

    public DiagnosticResult check(R model) throws DiagnosticCheckException {

        List<DiagnosticRuleResult.Error> errors = new ArrayList<>();
        List<DiagnosticRuleResult.Warn> warns = new ArrayList<>();

        for (DiagnosticRule<R> rule : rules) {
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
