package me.bottdev.kern.meta.core.diagnostic;

import java.util.List;

public record DiagnosticResult(
        List<DiagnosticRuleResult.Error> errors,
        List<DiagnosticRuleResult.Warn> warns
) {

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public boolean hasWarnings() {
        return !warns.isEmpty();
    }

}
