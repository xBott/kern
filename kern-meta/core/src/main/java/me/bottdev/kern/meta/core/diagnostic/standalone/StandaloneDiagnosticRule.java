package me.bottdev.kern.meta.core.diagnostic.standalone;

import me.bottdev.kern.meta.core.diagnostic.DiagnosticRule;
import me.bottdev.kern.meta.core.diagnostic.DiagnosticRuleResult;

@FunctionalInterface
public interface StandaloneDiagnosticRule extends DiagnosticRule {

    DiagnosticRuleResult check();

}
