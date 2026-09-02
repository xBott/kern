package me.bottdev.kern.commons.buffer;

import java.util.List;

/// A [Buffer] implementation representing a circular buffer (ring buffer).
/// When a RingBuffer reaches its capacity, adding new elements typically
/// overwrites the oldest elements.
public interface RingBuffer<T> extends Buffer<T> {

    /// Returns a consistent, ordered snapshot of the buffer's current state.
    /// Elements are ordered from oldest to newest.
    ///
    /// @return a list containing the elements in the buffer
    List<T> snapshot();

}