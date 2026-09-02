package me.bottdev.kern.dependency.versioned;

import lombok.NonNull;
import me.bottdev.kern.commons.wrapper.DiagnosticResult;
import me.bottdev.kern.dependency.DependencyDiagnostic;
import me.bottdev.kern.dependency.DependentContainer;
import me.bottdev.kern.dependency.ResolutionResult;

/// Interface that defines a strategy of versioned dependency resolution.
/// Should be used as a singleton.
public interface VersionedDependencyResolver {

    /// Resolves dependencies provided using [DependentContainer].
    ///
    /// @param dependentContainer a container with [VersionedDependencyAware] objects.
    /// @return diagnostic result with wrapped around resolution result.
    <K, T extends VersionedDependencyAware<K>> DiagnosticResult<ResolutionResult<K, T>, DependencyDiagnostic> resolve(
            @NonNull DependentContainer<K, T> dependentContainer
    );

}
