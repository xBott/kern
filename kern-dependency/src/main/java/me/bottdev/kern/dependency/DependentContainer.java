package me.bottdev.kern.dependency;

import java.util.List;

/// Container that stores a list of [DependencyAware] objects.
/// Used by resolver as an argument.
public interface DependentContainer<K, T extends DependencyAware<K>> {

    /// Returns a list of [[DependencyAware]] objects.
    List<T> dependents();

}
