package me.bottdev.kern.dependency.versioned;

import me.bottdev.kern.dependency.DependentContainer;
import me.bottdev.kern.dependency.ResolutionResult;

/// Interface that defines a strategy of dependency resolution with support of dependency versions
/// Should be used as a singleton.
/// Resolution does not fail fast: every problem found (missing dependencies, version mismatches,
/// version conflicts, cycles) is collected into a [ResolutionResult.Failure] instead of throwing
/// on the first one.
public interface VersionedDependencyResolver {

    /// Resolves versioned dependencies provided using [DependentContainer].
    ///
    /// @param dependentContainer a container with [VersionedDependencyAware] objects
    /// @return resolution result
    <K, T extends VersionedDependencyAware<K>> ResolutionResult<K, T> resolveVersioned(DependentContainer<K, T> dependentContainer);

}
