package me.bottdev.kern.meta.core.diagnostic.standalone;

import me.bottdev.kern.meta.core.diagnostic.DiagnosticBuilder;
import me.bottdev.kern.meta.core.diagnostic.DiagnosticResult;
import me.bottdev.kern.meta.core.diagnostic.DiagnosticRuleResult;
import me.bottdev.kern.meta.core.exceptions.DiagnosticCheckException;

import java.util.ArrayList;
import java.util.List;

public class StandaloneDiagnosticBuilder implements DiagnosticBuilder<StandaloneDiagnosticRule> {

    private final List<StandaloneDiagnosticRule> rules = new ArrayList<>();

    @Override
    public StandaloneDiagnosticBuilder rule(StandaloneDiagnosticRule rule) {
        rules.add(rule);
        return this;
    }

    @Override
    public DiagnosticResult build() {
        List<DiagnosticRuleResult.Error> errors = new ArrayList<>();
        List<DiagnosticRuleResult.Warn> warns = new ArrayList<>();

        for (StandaloneDiagnosticRule rule : rules) {
            try {

                switch (rule.check()) {
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
