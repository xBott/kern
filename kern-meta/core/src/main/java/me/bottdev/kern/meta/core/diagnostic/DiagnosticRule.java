package me.bottdev.kern.meta.core.diagnostic;

@FunctionalInterface
public interface DiagnosticRule<M> {

    DiagnosticRuleResult check(M model);

}
