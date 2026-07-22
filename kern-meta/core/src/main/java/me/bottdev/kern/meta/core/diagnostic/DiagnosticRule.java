package me.bottdev.kern.meta.core.diagnostic;

@FunctionalInterface
public interface DiagnosticRule<R> {

    DiagnosticRuleResult check(R model);

}
