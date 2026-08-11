package me.bottdev.kern.commons;

import lombok.RequiredArgsConstructor;

import java.util.function.Supplier;

/// Special utility-interface used for easy creation of **lazy** objects -
/// objects that are computed only when needed (on first call).
/// **Thread-save** - uses volatile and synchronization lock.
///
/// **Example**
/// ```java
/// Lazy<Integer> heavyValue = Lazy.of(() -> performHeavyComputation());
/// ```
@FunctionalInterface
public interface Lazy<T> {

    T compute();

    @RequiredArgsConstructor
    final class Simple<T> implements Lazy<T> {

        private final Supplier<T> supplier;
        private volatile boolean initialized = false;
        private volatile T value = null;

        @Override
        public T compute() {
            if (!initialized) {
                synchronized (this) {
                    value = supplier.get();
                    initialized = true;
                }
            }
            return value;
        }

    }

    /// Factory method for lazy object creation.
    /// @return [Simple] implementation of [Lazy] interface.
    static <T> Lazy<T> of(Supplier<T> supplier) {
        return new Simple<>(supplier);
    }

}
