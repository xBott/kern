package me.bottdev.kern.memory.pools;

import me.bottdev.kern.memory.DataBuffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class SlabDataBufferPoolTest {

    private SlabDataBufferPool pool;

    @BeforeEach
    void setUp() {
        pool = SlabDataBufferPool.builder()
                .addTier(64,   4)
                .addTier(256,  4)
                .addTier(1024, 4)
                .direct(false)
                .build();
    }

    @AfterEach
    void tearDown() {
        pool.close();
    }

    // ------------------------------------------------------------------
    //  Simple Allocation
    // ------------------------------------------------------------------

    @Nested
    class Allocation {

        @Test
        void allocate_returnsNonNull() {
            DataBuffer buf = pool.allocate(64);
            assertThat(buf).isNotNull();
            buf.release();
        }

        @Test
        void allocate_selectsSmallestFittingTier() {
            DataBuffer buf = pool.allocate(1);
            assertThat(buf.capacity()).isEqualTo(64);
            buf.release();
        }

        @Test
        void allocate_selectsMediumTier() {
            DataBuffer buf = pool.allocate(100);
            assertThat(buf.capacity()).isEqualTo(256);
            buf.release();
        }

        @Test
        void allocate_selectsLargeTier() {
            DataBuffer buf = pool.allocate(300);
            assertThat(buf.capacity()).isEqualTo(1024);
            buf.release();
        }

        @Test
        void allocate_exactTierBoundary() {
            DataBuffer buf = pool.allocate(256);
            assertThat(buf.capacity()).isEqualTo(256);
            buf.release();
        }

        @Test
        void allocate_exceedsMaxTier_throws() {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> pool.allocate(2048))
                    .withMessageContaining("exceeds the largest tier chunk size");
        }

        @Test
        void allocate_zeroCapacity_throws() {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> pool.allocate(0))
                    .withMessageContaining("minCapacity must be > 0");
        }

        @Test
        void allocate_negativeCapacity_throws() {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> pool.allocate(-1));
        }

        @Test
        void allocate_onClosedPool_throws() {
            pool.close();
            assertThatIllegalStateException()
                    .isThrownBy(() -> pool.allocate(64))
                    .withMessageContaining("Pool is closed");
        }

        @Test
        void allocate_bufferHasRefCountOne() {
            DataBuffer buf = pool.allocate(64);
            assertThat(buf.refCount()).isEqualTo(1);
            buf.release();
        }
    }

    // ------------------------------------------------------------------
    //  Pool release
    // ------------------------------------------------------------------

    @Nested
    class Recycle {

        @Test
        void release_decrementsActiveBuffers() {
            DataBuffer buf = pool.allocate(64);
            assertThat(pool.activeBuffers()).isEqualTo(1);
            buf.release();
            assertThat(pool.activeBuffers()).isZero();
        }

        @Test
        void release_allowsReuseOfChunk() {
            DataBuffer[] buffers = new DataBuffer[4];
            for (int i = 0; i < 4; i++) buffers[i] = pool.allocate(64);

            buffers[0].release();

            DataBuffer reused = pool.allocate(64);
            assertThat(pool.activeBuffers()).isEqualTo(4);
            reused.release();

            for (int i = 1; i < 4; i++) buffers[i].release();
        }

        @Test
        void recycle_unknownBufferType_throws() {
            DataBuffer foreign = new DataBuffer() {
                @Override public int readableBytes() { return 0; }
                @Override public int writableBytes() { return 0; }
                @Override public int capacity() { return 0; }
                @Override public byte readByte() { return 0; }
                @Override public short readShort() { return 0; }
                @Override public int readInt() { return 0; }
                @Override public long readLong() { return 0; }
                @Override public void readBytes(byte[] dst) {}
                @Override public void readBytes(byte[] dst, int offset, int length) {}
                @Override public DataBuffer writeByte(byte value) { return this; }
                @Override public DataBuffer writeShort(short value) { return this; }
                @Override public DataBuffer writeInt(int value) { return this; }
                @Override public DataBuffer writeLong(long value) { return this; }
                @Override public DataBuffer writeBytes(byte[] src) { return this; }
                @Override public DataBuffer writeBytes(byte[] src, int offset, int length) { return this; }
                @Override public byte getByte(int index) { return 0; }
                @Override public short getShort(int index) { return 0; }
                @Override public int getInt(int index) { return 0; }
                @Override public long getLong(int index) { return 0; }
                @Override public void setByte(int index, byte value) {}
                @Override public void setShort(int index, short value) {}
                @Override public void setInt(int index, int value) {}
                @Override public void setLong(int index, long value) {}
                @Override public int position() { return 0; }
                @Override public DataBuffer position(int p) { return this; }
                @Override public DataBuffer clear() { return this; }
                @Override public DataBuffer flip() { return this; }
                @Override public DataBuffer slice(int index, int length) { return this; }
                @Override public DataBuffer copy() { return this; }
                @Override public java.nio.ByteBuffer asByteBuffer() { return java.nio.ByteBuffer.allocate(0); }
                @Override public DataBuffer retain() { return this; }
                @Override public boolean release() { return true; }
                @Override public boolean isReleased() { return false; }
                @Override public int refCount() { return 1; }
            };

            assertThatIllegalArgumentException()
                    .isThrownBy(() -> pool.recycle(foreign))
                    .withMessageContaining("Unknown buffer type");
        }
    }

    // ------------------------------------------------------------------
    //  Automatic scaling
    // ------------------------------------------------------------------

    @Nested
    class AutoGrow {

        @Test
        void growsAutomatically_whenTierExhausted() {
            DataBuffer[] buffers = new DataBuffer[5];
            for (int i = 0; i < 5; i++) {
                int finalI = i;
                assertThatCode(() -> buffers[finalI] = pool.allocate(64)).doesNotThrowAnyException();
            }
            assertThat(pool.activeBuffers()).isEqualTo(5);
            for (DataBuffer b : buffers) b.release();
        }

        @Test
        void grownPool_recyclesCorrectly() {
            DataBuffer[] buffers = new DataBuffer[5];
            for (int i = 0; i < 5; i++) buffers[i] = pool.allocate(64);
            for (DataBuffer b : buffers) b.release();

            assertThat(pool.activeBuffers()).isZero();
        }
    }

    // ------------------------------------------------------------------
    //  Metrics
    // ------------------------------------------------------------------

    @Nested
    class Metrics {

        @Test
        void activeBuffers_startsAtZero() {
            assertThat(pool.activeBuffers()).isZero();
        }

        @Test
        void activeBuffers_incrementsOnAllocate() {
            DataBuffer b1 = pool.allocate(64);
            DataBuffer b2 = pool.allocate(64);
            assertThat(pool.activeBuffers()).isEqualTo(2);
            b1.release();
            b2.release();
        }

        @Test
        void totalMemory_isPositive() {
            assertThat(pool.totalMemory()).isPositive();
        }

        @Test
        void usedMemory_increasesOnAllocate() {
            long before = pool.usedMemory();
            DataBuffer buf = pool.allocate(64);
            assertThat(pool.usedMemory()).isGreaterThan(before);
            buf.release();
        }

        @Test
        void usedMemory_decreasesOnRelease() {
            DataBuffer buf = pool.allocate(64);
            long withBuf = pool.usedMemory();
            buf.release();
            assertThat(pool.usedMemory()).isLessThan(withBuf);
        }

        @Test
        void freeMemory_equalsTotalMinusUsed() {
            DataBuffer buf = pool.allocate(64);
            assertThat(pool.freeMemory()).isEqualTo(pool.totalMemory() - pool.usedMemory());
            buf.release();
        }
    }

    // ------------------------------------------------------------------
    //  Builder
    // ------------------------------------------------------------------

    @Nested
    class BuilderTests {

        @Test
        void build_withNoTiers_throws() {
            assertThatIllegalStateException()
                    .isThrownBy(() -> SlabDataBufferPool.builder().build())
                    .withMessageContaining("At least one tier is required");
        }

        @Test
        void build_withDuplicateTierSize_throws() {
            assertThatIllegalStateException()
                    .isThrownBy(() -> SlabDataBufferPool.builder()
                            .addTier(64, 4)
                            .addTier(64, 8)
                            .build())
                    .withMessageContaining("Duplicate tier chunkSize");
        }

        @Test
        void build_sortsTiersRegardlessOfAddOrder() {
            SlabDataBufferPool unordered = SlabDataBufferPool.builder()
                    .addTier(1024, 4)
                    .addTier(64,   4)
                    .addTier(256,  4)
                    .build();

            DataBuffer buf = unordered.allocate(1);
            assertThat(buf.capacity()).isEqualTo(64);
            buf.release();
            unordered.close();
        }

        @Test
        void addTier_zeroChunkSize_throws() {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> SlabDataBufferPool.builder().addTier(0, 4));
        }

        @Test
        void addTier_zeroSlabChunks_throws() {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> SlabDataBufferPool.builder().addTier(64, 0));
        }
    }

    // ------------------------------------------------------------------
    //  createDefault
    // ------------------------------------------------------------------

    @Nested
    class CreateDefault {

        @Test
        void createDefault_heap_allocatesCorrectly() {
            try (SlabDataBufferPool p = SlabDataBufferPool.createDefault(false)) {
                DataBuffer buf = p.allocate(4000);
                assertThat(buf.capacity()).isEqualTo(4096);
                buf.release();
                assertThat(p.activeBuffers()).isZero();
            }
        }

        @Test
        void createDefault_direct_allocatesCorrectly() {
            try (SlabDataBufferPool p = SlabDataBufferPool.createDefault(true)) {
                DataBuffer buf = p.allocate(64);
                assertThat(buf).isNotNull();
                buf.release();
            }
        }

        @Test
        void createDefault_totalMemory_matchesExpected() {
            try (SlabDataBufferPool p = SlabDataBufferPool.createDefault(false)) {
                // 64*256 + 256*128 + 1024*64 + 4096*32 + 16384*16
                long expected = 64L * 256 + 256L * 128 + 1024L * 64 + 4096L * 32 + 16384L * 16;
                assertThat(p.totalMemory()).isEqualTo(expected);
            }
        }
    }
}