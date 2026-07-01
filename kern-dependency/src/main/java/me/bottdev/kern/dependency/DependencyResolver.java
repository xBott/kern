package me.bottdev.kern.dependency;

import me.bottdev.kern.dependency.exceptions.DependencyException;
import me.bottdev.kern.struct.algorithms.sort.CircularDependencyException;

/// Interface that defines a strategy of dependency resolution.
/// Should be used as a singleton.
public interface DependencyResolver {

     /// Resolves dependencies provided using [DependentContainer].
     ///
     /// @param dependentContainer a container with [DependencyAware] objects
     /// @return resolution result
     ///
     /// @throws me.bottdev.kern.dependency.exceptions.MissingDependencyException if one of dependencies is missing
     /// @throws CircularDependencyException if circular dependency is found in the container
     <K, T extends DependencyAware<K>> ResolutionResult<T> resolve(DependentContainer<K, T> dependentContainer)
             throws DependencyException, CircularDependencyException;

}
