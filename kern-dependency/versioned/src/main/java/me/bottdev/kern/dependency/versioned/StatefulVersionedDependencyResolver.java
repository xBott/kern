package me.bottdev.kern.dependency.versioned;

import lombok.NonNull;
import me.bottdev.kern.commons.wrapper.DiagnosticResult;
import me.bottdev.kern.dependency.DependencyDiagnostic;
import me.bottdev.kern.dependency.DependencyResolverState;
import me.bottdev.kern.dependency.DependentContainer;
import me.bottdev.kern.dependency.ResolutionResult;

/// Interface that defines an object that performs versioned dependency resolution and
/// remembers state of the dependency graph.
public interface StatefulVersionedDependencyResolver<K, T extends VersionedDependencyAware<K>> {

    /// @return State of dependency resolver.
    @NonNull DependencyResolverState<K, T> state();

    /// Resolves provided dependents considering the context of the resolver.
    /// @return diagnostic result with resolution result of only provided objects.
    DiagnosticResult<ResolutionResult<K, T>, DependencyDiagnostic> resolveAndRemember(
            @NonNull DependentContainer<K, T> dependentContainer
    );

}
