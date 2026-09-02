package me.bottdev.kern.memory;

import java.io.Closeable;

/// Data Buffer Pool
///
/// Implementations must be thread-safe.
///
/// Do not call `release` directly — use [DataBuffer#release()].
/// Buffer will be automatically returned to the pool, when its reference counter reaches 0.
public interface DataBufferPool extends Closeable {

    /// Allocates a data buffer with capacity at least `minCapacity` bytes.
    /// Real capacity may be bigger because of slice alignment.
    ///
    /// @param minCapacity minimal required capacity in bytes (> 0)
    /// @return buffer with refCount == 1
    /// @throws IllegalArgumentException if `minCapacity <= 0`
    /// @throws OutOfMemoryError         if pool is exhausted and additional memory is not available
    DataBuffer allocate(int minCapacity);

    /// Returns a buffer to the pool. Called automatically from [DataBuffer#release()].
    /// Direct call is allowed only in implementations of the buffer itself.
    void recycle(DataBuffer buffer);

    /// Total volume of memory, reserved by pool (bytes).
    long totalMemory();

    /// Volume of memory, occupied by active buffers (bytes).
    long usedMemory();

    /// Volume of free memory in the pool (bytes).
    default long freeMemory() {
        return totalMemory() - usedMemory();
    }

    /// Amount of active (not released) buffers.
    int activeBuffers();


    /// Releases all resources of the pool.
    /// After the call, any operations on the pool and the allocated buffers are undefined.
    @Override
    void close();
}