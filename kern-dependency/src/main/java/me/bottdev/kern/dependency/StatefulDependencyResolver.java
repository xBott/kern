package me.bottdev.kern.dependency;

import me.bottdev.kern.commons.wrapper.DiagnosticResult;

/// Interface that defines an object that performs dependency resolution and
/// remembers state of the dependency graph.
/// Completely different from [DependencyResolver] semantically. Unlike [DependencyResolver],
/// this interfaces represents an object that is bound to a specific type of object and has a state.
/// Allows to resolve dependencies gradually with context.
/// Resolution does not fail fast: every problem found (missing dependencies, version mismatches,
/// version conflicts, cycles) is collected into a [DiagnosticResult.Failure] instead of throwing
/// on the first one.
public interface StatefulDependencyResolver<K, T extends DependencyAware<K>> {

    /// @return State of dependency resolver.
    DependencyResolverState<K, T> state();

    /// Resolves provided dependents considering the context of the resolver.
    /// @return diagnostic result with resolution result of only provided objects.
    DiagnosticResult<ResolutionResult<K, T>, DependencyDiagnostic> resolveAndRemember(
            DependentContainer<K, T> dependentContainer
    );

}
