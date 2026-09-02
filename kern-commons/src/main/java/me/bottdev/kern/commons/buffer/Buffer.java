package me.bottdev.kern.commons.buffer;

import java.util.Collection;

/// Base interface for bounded buffer structures.
public interface Buffer<T> extends Collection<T> {

    /// @return the maximum number of elements this buffer can hold
    int capacity();

    /// @return true if the buffer has reached its capacity
    default boolean isFull() {
        return size() >= capacity();
    }
}
