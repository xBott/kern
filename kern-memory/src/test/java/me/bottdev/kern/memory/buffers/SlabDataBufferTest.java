package me.bottdev.kern.memory.buffers;

import me.bottdev.kern.memory.DataBuffer;
import me.bottdev.kern.memory.DataBufferPool;
import me.bottdev.kern.memory.Slab;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static org.assertj.core.api.Assertions.*;

class SlabDataBufferTest {

    private static final int CHUNK_SIZE = 64;

    private Slab slab;
    private SlabDataBuffer buffer;


    private DataBufferPool stubPool(Slab slab) {
        return new DataBufferPool() {
            @Override public DataBuffer allocate(int minCapacity) {
                int idx = slab.allocateIndex();
                return new SlabDataBuffer(slab.slice(idx), slab, idx, this);
            }
            @Override public void recycle(DataBuffer buf) {
                slab.free(((SlabDataBuffer) buf).chunkIndex());
            }
            @Override public long totalMemory()  { return 0; }
            @Override public long usedMemory()   { return 0; }
            @Override public int  activeBuffers(){ return 0; }
            @Override public void close()        {}
        };
    }

    @BeforeEach
    void setUp() {
        slab = new Slab(ByteBuffer.allocate(CHUNK_SIZE * 4), CHUNK_SIZE);
        DataBufferPool pool = stubPool(slab);
        int index = slab.allocateIndex();
        buffer = new SlabDataBuffer(slab.slice(index), slab, index, pool);
    }

    @Test
    void capacity_equalsChunkSize() {
        assertThat(buffer.capacity()).isEqualTo(CHUNK_SIZE);
    }

    @Test
    void initialPosition_isZero() {
        assertThat(buffer.position()).isZero();
    }

    @Test
    void initialReadableBytes_equalsCapacity() {
        assertThat(buffer.readableBytes()).isEqualTo(CHUNK_SIZE);
    }

    @Test
    void initialWritableBytes_isZero() {
        assertThat(buffer.writableBytes()).isZero();
    }

    @Test
    void position_movesCorrectly() {
        buffer.position(10);
        assertThat(buffer.position()).isEqualTo(10);
    }

    @Test
    void position_returnsThis_forChaining() {
        assertThat(buffer.position(5)).isSameAs(buffer);
    }

    @Test
    void clear_resetsPositionAndLimit() {
        buffer.writeInt(42);
        buffer.flip();
        buffer.clear();

        assertThat(buffer.position()).isZero();
        assertThat(buffer.readableBytes()).isEqualTo(CHUNK_SIZE);
    }

    @Test
    void flip_switchesToReadMode() {
        buffer.writeInt(1).writeInt(2);
        buffer.flip();

        assertThat(buffer.position()).isZero();
        assertThat(buffer.readableBytes()).isEqualTo(8);
    }

    @Test
    void writeByte_readByte_roundtrip() {
        buffer.writeByte((byte) 0x7F);
        buffer.flip();
        assertThat(buffer.readByte()).isEqualTo((byte) 0x7F);
    }

    @Test
    void writeShort_readShort_roundtrip() {
        buffer.writeShort((short) 32_000);
        buffer.flip();
        assertThat(buffer.readShort()).isEqualTo((short) 32_000);
    }

    @Test
    void writeInt_readInt_roundtrip() {
        buffer.writeInt(Integer.MAX_VALUE);
        buffer.flip();
        assertThat(buffer.readInt()).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    void writeLong_readLong_roundtrip() {
        buffer.writeLong(Long.MIN_VALUE);
        buffer.flip();
        assertThat(buffer.readLong()).isEqualTo(Long.MIN_VALUE);
    }

    @Test
    void writeBytes_readBytes_fullArray() {
        byte[] src = {1, 2, 3, 4, 5};
        buffer.writeBytes(src);
        buffer.flip();

        byte[] dst = new byte[src.length];
        buffer.readBytes(dst);
        assertThat(dst).isEqualTo(src);
    }

    @Test
    void writeBytes_readBytes_withOffsetAndLength() {
        byte[] src = {0, 1, 2, 3, 4, 5, 6};
        buffer.writeBytes(src, 2, 3);
        buffer.flip();

        byte[] dst = new byte[3];
        buffer.readBytes(dst, 0, 3);
        assertThat(dst).containsExactly(2, 3, 4);
    }

    @Test
    void write_methods_returnThis_forChaining() {
        assertThat(buffer.writeByte((byte) 1)).isSameAs(buffer);
        assertThat(buffer.writeShort((short) 2)).isSameAs(buffer);
        assertThat(buffer.writeInt(3)).isSameAs(buffer);
        assertThat(buffer.writeLong(4L)).isSameAs(buffer);
        assertThat(buffer.writeBytes(new byte[]{5})).isSameAs(buffer);
    }

    @Test
    void setGetByte_doesNotMovePosition() {
        buffer.setByte(0, (byte) 0xAB);
        assertThat(buffer.position()).isZero();
        assertThat(buffer.getByte(0)).isEqualTo((byte) 0xAB);
    }

    @Test
    void setGetShort_correctValue() {
        buffer.setShort(0, (short) -1000);
        assertThat(buffer.getShort(0)).isEqualTo((short) -1000);
    }

    @Test
    void setGetInt_correctValue() {
        buffer.setInt(4, 0xDEADBEEF);
        assertThat(buffer.getInt(4)).isEqualTo(0xDEADBEEF);
    }

    @Test
    void setGetLong_correctValue() {
        buffer.setLong(0, Long.MAX_VALUE);
        assertThat(buffer.getLong(0)).isEqualTo(Long.MAX_VALUE);
    }


    @Test
    void asByteBuffer_returnsDuplicate_notSameReference() {
        ByteBuffer bb = buffer.asByteBuffer();
        assertThat(bb).isNotSameAs(buffer.asByteBuffer());
    }

    @Test
    void asByteBuffer_positionChangesDoNotAffectBuffer() {
        buffer.writeInt(42);
        ByteBuffer bb = buffer.asByteBuffer();
        bb.position(bb.limit());
        assertThat(buffer.position()).isEqualTo(4);
    }

    @Nested
    class SliceTests {

        @Test
        void slice_sharesMemory_withParent() {
            buffer.setInt(0, 0xCAFEBABE);
            DataBuffer slice = buffer.slice(0, 4);

            assertThat(slice.getInt(0)).isEqualTo(0xCAFEBABE);

            slice.setInt(0, 0x12345678);
            assertThat(buffer.getInt(0)).isEqualTo(0x12345678);

            slice.release();
        }

        @Test
        void slice_incrementsParentRefCount() {
            assertThat(buffer.refCount()).isEqualTo(1);
            DataBuffer slice = buffer.slice(0, 8);
            assertThat(buffer.refCount()).isEqualTo(2);
            slice.release();
            assertThat(buffer.refCount()).isEqualTo(1);
        }

        @Test
        void slice_releaseDecrementsParentRefCount() {
            DataBuffer slice = buffer.slice(0, 8);
            slice.release();
            assertThat(buffer.isReleased()).isFalse();
            assertThat(buffer.refCount()).isEqualTo(1);
        }

        @Test
        void slice_hasCorrectCapacity() {
            DataBuffer slice = buffer.slice(10, 20);
            assertThat(slice.capacity()).isEqualTo(20);
            slice.release();
        }

        @Test
        void slice_outOfBounds_throws() {
            assertThatExceptionOfType(IndexOutOfBoundsException.class)
                    .isThrownBy(() -> buffer.slice(60, 10));
        }

        @Test
        void slice_negativeIndex_throws() {
            assertThatExceptionOfType(IndexOutOfBoundsException.class)
                    .isThrownBy(() -> buffer.slice(-1, 4));
        }

        @Test
        void slice_negativeLength_throws() {
            assertThatExceptionOfType(IndexOutOfBoundsException.class)
                    .isThrownBy(() -> buffer.slice(0, -1));
        }
    }

    @Nested
    class CopyTests {

        @Test
        void copy_containsSameData() {
            buffer.writeInt(0xBEEF);
            buffer.flip();

            DataBuffer copy = buffer.copy();
            assertThat(copy.readInt()).isEqualTo(0xBEEF);
            copy.release();
        }

        @Test
        void copy_isIndependentFromOriginal() {
            buffer.setInt(0, 42);

            DataBuffer copy = buffer.copy();
            copy.setInt(0, 99);

            assertThat(buffer.getInt(0)).isEqualTo(42);
            copy.release();
        }

        @Test
        void copy_doesNotAffectOriginalPosition() {
            buffer.writeInt(1);
            int posBefore = buffer.position();

            DataBuffer copy = buffer.copy();
            assertThat(buffer.position()).isEqualTo(posBefore);
            copy.release();
        }
    }

    @Nested
    class RefCountTests {

        @Test
        void initialRefCount_isOne() {
            assertThat(buffer.refCount()).isEqualTo(1);
        }

        @Test
        void retain_incrementsRefCount() {
            buffer.retain();
            assertThat(buffer.refCount()).isEqualTo(2);
            buffer.release();
        }

        @Test
        void retain_returnsThis() {
            assertThat(buffer.retain()).isSameAs(buffer);
            buffer.release();
        }

        @Test
        void release_returnsFalse_whenRefCountAboveZero() {
            buffer.retain();
            assertThat(buffer.release()).isFalse();
            assertThat(buffer.isReleased()).isFalse();
        }

        @Test
        void release_returnsTrue_whenRefCountReachesZero() {
            assertThat(buffer.release()).isTrue();
            assertThat(buffer.isReleased()).isTrue();
        }

        @Test
        void release_recyclesChunkToSlab() {
            int usedBefore = slab.usedChunks();
            buffer.release();
            assertThat(slab.usedChunks()).isEqualTo(usedBefore - 1);
        }

        @Test
        void doubleRelease_throws() {
            buffer.release();
            assertThatIllegalStateException()
                    .isThrownBy(() -> buffer.release())
                    .withMessageContaining("release() called too many times");
        }

        @Test
        void retainAfterRelease_throws() {
            buffer.release();
            assertThatIllegalStateException()
                    .isThrownBy(() -> buffer.retain())
                    .withMessageContaining("retain() called on released buffer");
        }
    }

    @Nested
    class UseAfterFreeTests {

        @Test
        void readByte_afterRelease_throws() {
            buffer.release();
            assertThatIllegalStateException().isThrownBy(buffer::readByte);
        }

        @Test
        void writeInt_afterRelease_throws() {
            buffer.release();
            assertThatIllegalStateException().isThrownBy(() -> buffer.writeInt(1));
        }

        @Test
        void slice_afterRelease_throws() {
            buffer.release();
            assertThatIllegalStateException().isThrownBy(() -> buffer.slice(0, 4));
        }

        @Test
        void asByteBuffer_afterRelease_throws() {
            buffer.release();
            assertThatIllegalStateException().isThrownBy(buffer::asByteBuffer);
        }

        @Test
        void position_afterRelease_throws() {
            buffer.release();
            assertThatIllegalStateException().isThrownBy(buffer::position);
        }
    }

    @Test
    void toString_containsUsefulInfo() {
        assertThat(buffer.toString())
                .contains("SlabDataBuffer")
                .contains("capacity=" + CHUNK_SIZE)
                .contains("refCount=1");
    }

}