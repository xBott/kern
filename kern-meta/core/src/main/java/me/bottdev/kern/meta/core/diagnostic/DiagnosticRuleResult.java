package me.bottdev.kern.meta.core.diagnostic;

public sealed interface DiagnosticRuleResult permits
        DiagnosticRuleResult.OK,
        DiagnosticRuleResult.Error,
        DiagnosticRuleResult.Warn
{

    static DiagnosticRuleResult.OK ok() {
        return new OK();
    }

    static DiagnosticRuleResult.Error error(String message) {
        return new Error(message);
    }

    static DiagnosticRuleResult.Warn warn(String message) {
        return new Warn(message);
    }

    final class OK implements DiagnosticRuleResult {}

    record Error(String message) implements DiagnosticRuleResult {}

    record Warn(String message) implements DiagnosticRuleResult {}

}
