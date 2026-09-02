package me.bottdev.kern.memory.pools;

import me.bottdev.kern.memory.DataBuffer;
import me.bottdev.kern.memory.DataBufferPool;
import me.bottdev.kern.memory.Slab;
import me.bottdev.kern.memory.buffers.SlabDataBuffer;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

/// A multi-level buffer pool based on [Slab].
///
/// Supports multiple ‘layers’ with different chunk sizes (e.g. 64, 256, 1024, 4096 bytes).
/// When a buffer is requested, the layer with the smallest suitable chunkSize is selected.
/// If all slabs in a layer are full, a new slab is created automatically.
///
/// **Thread-safe.**
///
/// ### Example of creation:
/// ```java
/// DataBufferPool pool = SlabDataBufferPool.builder()
///         .addTier(64, 128)     // 128 chunks of 64 bytes each   = 8 KB
///         .addTier(256, 64)     // 64  chunks of 256 bytes   = 16 KB
///         .addTier(1024, 32)    // 32  chunks of 1 KB       = 32 KB
///         .addTier(4096, 16)    // 16 chunks of 4 KB each       = 64 KB
///         .direct(true)         // use DirectByteBuffer
///         .build();
/// ```
public final class SlabDataBufferPool implements DataBufferPool {

    /// One level: set of slabs with the same chunkSize.
    private static final class Tier {
        final int chunkSize;
        final int slabChunks;
        final boolean direct;

        private volatile Slab[] slabs;

        Tier(int chunkSize, int slabChunks, boolean direct) {
            this.chunkSize = chunkSize;
            this.slabChunks = slabChunks;
            this.direct = direct;
            this.slabs = new Slab[]{ newSlab() };
        }

        private Slab newSlab() {
            int bytes = chunkSize * slabChunks;
            ByteBuffer mem = direct
                    ? ByteBuffer.allocateDirect(bytes)
                    : ByteBuffer.allocate(bytes);
            return new Slab(mem, chunkSize);
        }

        /// Tries to allocate a chunk in any of existing slabs.
        /// If all slabs are occupied - creates a new slab.
        ///
        /// @return [SlabDataBuffer] with refCount == 1
        synchronized SlabDataBuffer allocate(DataBufferPool pool) {

            for (Slab slab : slabs) {
                int index = slab.allocateIndex();
                if (index >= 0) {
                    return new SlabDataBuffer(slab.slice(index), slab, index, pool);
                }
            }

            Slab fresh = newSlab();
            Slab[] grown = Arrays.copyOf(slabs, slabs.length + 1);
            grown[grown.length - 1] = fresh;
            slabs = grown;

            int idx = fresh.allocateIndex();
            return new SlabDataBuffer(fresh.slice(idx), fresh, idx, pool);
        }

        /// Total capacity of the tier in bytes.
        synchronized long totalBytes() {
            long sum = 0;
            for (Slab s : slabs) sum += s.totalBytes();
            return sum;
        }

        /// Occupied volume of tier in bytes.
        synchronized long usedBytes() {
            long sum = 0;
            for (Slab s : slabs) sum += s.usedBytes();
            return sum;
        }

        /// Amount of active buffers in the tier.
        synchronized int activeBuffers() {
            int sum = 0;
            for (Slab s : slabs) sum += s.usedChunks();
            return sum;
        }
    }

    private final Tier[]   tiers;     // sorted by chunk size
    private final AtomicInteger activeBufferCount = new AtomicInteger(0);
    private volatile boolean closed = false;

    private SlabDataBufferPool(Tier[] tiers) {
        this.tiers = tiers;
    }

    @Override
    public DataBuffer allocate(int minCapacity) {
        if (minCapacity <= 0) throw new IllegalArgumentException("minCapacity must be > 0");
        checkNotClosed();

        Tier tier = findTier(minCapacity);
        SlabDataBuffer buf = tier.allocate(this);
        activeBufferCount.incrementAndGet();
        return buf;
    }

    @Override
    public void recycle(DataBuffer buffer) {
        if (!(buffer instanceof SlabDataBuffer slab)) {
            throw new IllegalArgumentException("Unknown buffer type: " + buffer.getClass());
        }
        slab.slab().free(slab.chunkIndex());
        activeBufferCount.decrementAndGet();
    }

    @Override
    public long totalMemory() {
        long sum = 0;
        for (Tier t : tiers) sum += t.totalBytes();
        return sum;
    }

    @Override
    public long usedMemory() {
        long sum = 0;
        for (Tier t : tiers) sum += t.usedBytes();
        return sum;
    }

    @Override
    public int activeBuffers() {
        return activeBufferCount.get();
    }

    @Override
    public void close() {
        closed = true;
    }

    private Tier findTier(int minCapacity) {
        for (Tier t : tiers) {
            if (t.chunkSize >= minCapacity) return t;
        }

        throw new IllegalArgumentException(
                "Requested capacity " + minCapacity
                        + " exceeds the largest tier chunk size "
                        + tiers[tiers.length - 1].chunkSize
                        + ". Use a larger tier or increase max tier size.");
    }

    private void checkNotClosed() {
        if (closed) throw new IllegalStateException("Pool is closed");
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private record TierSpec(int chunkSize, int slabChunks) {}

        private final java.util.List<TierSpec> specs = new java.util.ArrayList<>();
        private boolean direct = false;

        /// Adds a tier with specified chunk size and amount of chunks in every slab.
        ///
        /// @param chunkSize  size of single buffer in bytes
        /// @param slabChunks amount of chunks in every slab of this tier
        public Builder addTier(int chunkSize, int slabChunks) {
            if (chunkSize <= 0)  throw new IllegalArgumentException("chunkSize must be > 0");
            if (slabChunks <= 0) throw new IllegalArgumentException("slabChunks must be > 0");
            specs.add(new TierSpec(chunkSize, slabChunks));
            return this;
        }

        /// Use `ByteBuffer.allocateDirect` instead of heap-buffers.
        /// By default `false`.
        public Builder direct(boolean direct) {
            this.direct = direct;
            return this;
        }

        public SlabDataBufferPool build() {
            if (specs.isEmpty()) throw new IllegalStateException("At least one tier is required");

            specs.sort(java.util.Comparator.comparingInt(TierSpec::chunkSize));

            for (int i = 1; i < specs.size(); i++) {
                if (specs.get(i).chunkSize() == specs.get(i - 1).chunkSize()) {
                    throw new IllegalStateException(
                            "Duplicate tier chunkSize: " + specs.get(i).chunkSize());
                }
            }

            final boolean d = direct;
            Tier[] tiers = specs.stream()
                    .map(s -> new Tier(s.chunkSize(), s.slabChunks(), d))
                    .toArray(Tier[]::new);

            return new SlabDataBufferPool(tiers);
        }
    }


    /// Creates a pool with different tiers bu default.
    /// Suitable for the most of Network / IO operations.
    /// <pre>
    ///  Tier   64 B   × 256 chunks  = 16 KB
    ///  Tier  256 B   × 128 chunks  = 32 KB
    ///  Tier   1 KB  ×  64 chunks  = 64 KB
    ///  Tier   4 KB  ×  32 chunks  = 128 KB
    ///  Tier  16 KB  ×  16 chunks  = 256 KB
    /// </pre>
    public static SlabDataBufferPool createDefault(boolean direct) {
        return builder()
                .addTier(64,        256)
                .addTier(256,       128)
                .addTier(1024,      64)
                .addTier(4096,      32)
                .addTier(16384,     16)
                .direct(direct)
                .build();
    }
}