package me.bottdev.kern.memory;

import java.nio.ByteBuffer;
import java.util.BitSet;
import java.util.concurrent.atomic.AtomicInteger;

/// A contiguous block of memory divided into fixed-size chunks.
///
/// Thread-safe: allocation and deallocation are protected using `synchronized`.
public final class Slab {

    private final ByteBuffer memory;
    private final int chunkSize;
    private final int capacity;
    private final BitSet used;
    private final AtomicInteger usedCount = new AtomicInteger(0);

    public Slab(ByteBuffer memory, int chunkSize) {
        if (memory == null)    throw new IllegalArgumentException("memory must not be null");
        if (chunkSize <= 0)    throw new IllegalArgumentException("chunkSize must be > 0");
        if (memory.capacity() < chunkSize)
            throw new IllegalArgumentException("memory capacity is smaller than chunkSize");

        this.memory    = memory;
        this.chunkSize = chunkSize;
        this.capacity  = memory.capacity() / chunkSize;
        this.used      = new BitSet(capacity);
    }

    /// Books next available chunk.
    ///
    /// @return chunks's index, or `-1` if slab is full
    public synchronized int allocateIndex() {
        int idx = used.nextClearBit(0);
        if (idx >= capacity) return -1;

        used.set(idx);
        usedCount.incrementAndGet();
        return idx;
    }

    /// Releases the chunk by index.
    ///
    /// @throws IllegalArgumentException if index is out of bounds
    /// @throws IllegalStateException    if chunk is already released (double-free)
    public synchronized void free(int index) {
        checkIndex(index);
        if (!used.get(index)) {
            throw new IllegalStateException(
                    "Double-free detected for chunk index " + index + " in slab " + this);
        }
        used.clear(index);
        usedCount.decrementAndGet();
    }

    /// Returns a [ByteBuffer], pointing to the chunk with specified index.
    /// Every call creates new view — change of position/limit does not affect other views.
    public ByteBuffer slice(int index) {
        checkIndex(index);
        int pos = index * chunkSize;

        ByteBuffer dup = memory.duplicate();
        dup.position(pos);
        dup.limit(pos + chunkSize);
        return dup.slice();
    }

    /// @return `true` if all chunks are occupied
    public boolean isFull() {
        return usedCount.get() == capacity;
    }

    /// @return `true` if all chunks are available
    public boolean isEmpty() {
        return usedCount.get() == 0;
    }

    /// Amount of occupied chunks.
    public int usedChunks() {
        return usedCount.get();
    }

    /// Total amount of chunks.
    public int capacity() {
        return capacity;
    }

    /// Size of a single chunk in bytes.
    public int chunkSize() {
        return chunkSize;
    }

    /// Total capacity of the slab in bytes.
    public int totalBytes() {
        return capacity * chunkSize;
    }

    /// Occupied volume in bytes.
    public int usedBytes() {
        return usedCount.get() * chunkSize;
    }


    private void checkIndex(int index) {
        if (index < 0 || index >= capacity) {
            throw new IllegalArgumentException(
                    "Chunk index " + index + " out of range [0, " + capacity + ")");
        }
    }

    @Override
    public String toString() {
        return "Slab{chunkSize=" + chunkSize
                + ", capacity=" + capacity
                + ", used=" + usedCount.get()
                + '}';
    }

}