package me.bottdev.kern.meta.core.diagnostic.bound;

import me.bottdev.kern.meta.core.diagnostic.DiagnosticRule;
import me.bottdev.kern.meta.core.diagnostic.DiagnosticRuleResult;

@FunctionalInterface
public interface BoundDiagnosticRule<M> extends DiagnosticRule {

    DiagnosticRuleResult check(M model);

}
