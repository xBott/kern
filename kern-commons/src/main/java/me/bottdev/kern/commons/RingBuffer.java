package me.bottdev.kern.commons;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.Consumer;

/// **Lock-free**, **thread-safe** ring buffer (MPMC, overwriting).
/// Guarantees:
///  - add() never blocks or waits
///  - On overflow, old data is silently overwritten
///  - snapshot()/forEach() provide a consistent snapshot at the time of the call,
///    but may not include elements added in parallel during the iteration
/// Algorithm:
///  - writeIndex — a monotonically increasing AtomicLong (no wraparound)
///  - Actual position in the array: index & (capacity - 1)
///  - Each slot stores a sequence — an even value means “occupied and ready to read”
///  - Pattern taken from Disruptor / JCTools
public class RingBuffer<T> {

    private static final int RETRY_LIMIT = 64;

    private final AtomicReferenceArray<Object> data;
    private final AtomicLong[] sequences;
    private final int capacity;
    private final int mask;

    private final AtomicLong writeCounter = new AtomicLong(0);

    public RingBuffer(int capacity) {

        if (capacity <= 0)
            throw new IllegalArgumentException("Capacity must be positive");
        if (Integer.bitCount(capacity) != 1)
            throw new IllegalArgumentException("Capacity must be a power of two");

        this.capacity = capacity;
        this.mask = capacity - 1;
        this.data = new AtomicReferenceArray<>(capacity);
        this.sequences = new AtomicLong[capacity];

        for (int i = 0; i < capacity; i++) {
            sequences[i] = new AtomicLong((long) i * 2);
        }
    }

    public int capacity() {
        return capacity;
    }

    /// Return an approximate size.
    /// It is not possible to ensure precise value in MPMC without locks
    public int size() {
        long w = writeCounter.get();
        return (int) Math.min(w, capacity);
    }

    public boolean isEmpty() {
        return writeCounter.get() == 0;
    }

    /// Adds an element. If buffer is full - overwrites the oldest element.
    /// Never blocks.
    public void add(T object) {
        long counter = writeCounter.getAndIncrement();
        int slot = (int) (counter & mask);
        long expectedSeq = counter * 2;

        AtomicLong seq = sequences[slot];
        int retries = 0;

        while (true) {

            long s = seq.get();
            if (s == expectedSeq) break;

            if (retries++ > RETRY_LIMIT) {
                seq.compareAndSet(s, expectedSeq);
                break;
            }

            Thread.onSpinWait();
        }

        seq.set(expectedSeq + 1);
        data.set(slot, object);
        seq.set(expectedSeq + 2);
    }

    /// Iterates all the current elements from the oldest to the newest.
    /// Snapshot is made atomically using writeIndex.
    public void forEach(Consumer<T> consumer) {
        long end = writeCounter.get();
        long start = Math.max(0, end - capacity);

        for (long counter = start; counter < end; counter++) {
            int slot = (int) (counter & mask);
            T value = readSlot(slot, counter);
            if (value != null) {
                consumer.accept(value);
            }
        }
    }

    /**
     * Returns a consistent snapshot of the buffer (from old to new).
     */
    public List<T> snapshot() {
        long end = writeCounter.get();
        long start = Math.max(0, end - capacity);
        List<T> result = new ArrayList<>((int) (end - start));

        for (long counter = start; counter < end; counter++) {
            int slot = (int) (counter & mask);
            T value = readSlot(slot, counter);
            if (value != null) {
                result.add(value);
            }
        }
        return result;
    }

    /**
     * Clears the buffer. Not atomic: call only when there are no active writers.
     */
    public void clear() {
        writeCounter.set(0);
        for (int i = 0; i < capacity; i++) {
            data.set(i, null);
            sequences[i].set((long) i * 2);
        }
    }

    /// Reads a slot, checking that sequence corresponds to the expected counter.
    /// If data is not written yet or already overwritten - returns null.
    @SuppressWarnings("unchecked")
    private T readSlot(int slot, long counter) {
        long expectedSeq = counter * 2 + 2;
        int retries = 0;

        while (true) {
            long s = sequences[slot].get();
            if (s == expectedSeq) {
                return (T) data.get(slot);

            }
            if (s > expectedSeq || retries++ > RETRY_LIMIT) {
                return null;

            }
            Thread.onSpinWait();
        }
    }
}