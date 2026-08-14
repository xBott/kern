package me.bottdev.kern.dependency;

import me.bottdev.kern.commons.wrapper.DiagnosticResult;

/// Interface that defines a strategy of dependency resolution.
/// Should be used as a singleton.
/// Resolution does not fail fast: every problem found (missing dependencies, version mismatches,
/// version conflicts, cycles) is collected into a [DiagnosticResult.Failure] instead of throwing
/// on the first one.
public interface DependencyResolver {

     /// Resolves dependencies provided using [DependentContainer].
     ///
     /// @param dependentContainer a container with [DependencyAware] objects.
     /// @return diagnostic result with wrapped around resolution result.
     <K, T extends DependencyAware<K>> DiagnosticResult<ResolutionResult<K, T>, DependencyDiagnostic> resolve(DependentContainer<K, T> dependentContainer);

}
