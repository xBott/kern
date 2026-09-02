package me.bottdev.kern.memory.buffers;

import me.bottdev.kern.memory.DataBuffer;
import me.bottdev.kern.memory.DataBufferPool;
import me.bottdev.kern.memory.Slab;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.atomic.AtomicInteger;

/// Implementation of [DataBuffer] on a single chunk of [Slab].
///
/// Lifecycle is controlled by counting references: when refCount == 0
/// chunk is automatically released to the pool using [DataBufferPool#recycle].
///
/// **NOT Thread-safe** on the level of reading/writing. Synchronization is on the level of calling code.
public final class SlabDataBuffer implements DataBuffer {

    private final ByteBuffer buffer;
    private final Slab slab;
    private final int chunkIndex;
    private final DataBufferPool pool;

    private final AtomicInteger refCount = new AtomicInteger(1);

    /// Flag «this is a slice»: slices do not release the chunk to slab, they decrement
    /// refCount of parent buffer.
    private final SlabDataBuffer parent;

    /// Constructor for root buffer
    public SlabDataBuffer(ByteBuffer buffer, Slab slab, int chunkIndex, DataBufferPool pool) {
        this.buffer = buffer.order(ByteOrder.BIG_ENDIAN);
        this.slab = slab;
        this.chunkIndex = chunkIndex;
        this.pool = pool;
        this.parent = null;
    }

    /// Constructor for slice.
    private SlabDataBuffer(ByteBuffer buffer, SlabDataBuffer parent) {
        this.buffer = buffer.order(ByteOrder.BIG_ENDIAN);
        this.slab = parent.slab;
        this.chunkIndex = parent.chunkIndex;
        this.pool = parent.pool;
        this.parent = parent;
        parent.retain();
    }


    @Override public int readableBytes() { checkNotReleased(); return buffer.limit() - buffer.position(); }
    @Override public int writableBytes() { checkNotReleased(); return buffer.capacity() - buffer.limit();  }
    @Override public int capacity() { return buffer.capacity(); }
    @Override public int position() { checkNotReleased(); return buffer.position(); }

    @Override
    public DataBuffer position(int newPosition) {
        checkNotReleased();
        buffer.position(newPosition);
        return this;
    }

    @Override
    public DataBuffer clear() {
        checkNotReleased();
        buffer.clear();
        return this;
    }

    @Override
    public DataBuffer flip() {
        checkNotReleased();
        buffer.flip();
        return this;
    }

    @Override public byte readByte() { checkNotReleased(); return buffer.get(); }
    @Override public short readShort() { checkNotReleased(); return buffer.getShort(); }
    @Override public int readInt() { checkNotReleased(); return buffer.getInt(); }
    @Override public long readLong() { checkNotReleased(); return buffer.getLong(); }

    @Override
    public void readBytes(byte[] dst) {
        readBytes(dst, 0, dst.length);
    }

    @Override
    public void readBytes(byte[] dst, int offset, int length) {
        checkNotReleased();
        buffer.get(dst, offset, length);
    }

    @Override public DataBuffer writeByte(byte value) { checkNotReleased(); buffer.put(value); return this; }
    @Override public DataBuffer writeShort(short value) { checkNotReleased(); buffer.putShort(value); return this; }
    @Override public DataBuffer writeInt(int value) { checkNotReleased(); buffer.putInt(value); return this; }
    @Override public DataBuffer writeLong(long value) { checkNotReleased(); buffer.putLong(value); return this; }

    @Override
    public DataBuffer writeBytes(byte[] src) {
        return writeBytes(src, 0, src.length);
    }

    @Override
    public DataBuffer writeBytes(byte[] src, int offset, int length) {
        checkNotReleased();
        buffer.put(src, offset, length);
        return this;
    }

    @Override public byte getByte(int i) { checkNotReleased(); return buffer.get(i); }
    @Override public short getShort(int i) { checkNotReleased(); return buffer.getShort(i); }
    @Override public int getInt(int i) { checkNotReleased(); return buffer.getInt(i); }
    @Override public long getLong(int i) { checkNotReleased(); return buffer.getLong(i); }

    @Override public void setByte(int i, byte v) { checkNotReleased(); buffer.put(i, v); }
    @Override public void setShort(int i, short v) { checkNotReleased(); buffer.putShort(i, v); }
    @Override public void setInt(int i, int v) { checkNotReleased(); buffer.putInt(i, v); }
    @Override public void setLong(int i, long v) { checkNotReleased(); buffer.putLong(i, v); }

    @Override
    public DataBuffer slice(int index, int length) {
        checkNotReleased();
        if (index < 0 || length < 0 || index + length > buffer.capacity()) {
            throw new IndexOutOfBoundsException(
                    "slice(" + index + ", " + length + ") out of capacity " + buffer.capacity());
        }
        ByteBuffer sliceBuf = buffer.duplicate();
        sliceBuf.position(index);
        sliceBuf.limit(index + length);
        return new SlabDataBuffer(sliceBuf.slice(), this);
    }

    @Override
    public DataBuffer copy() {
        checkNotReleased();
        int savedPos   = buffer.position();
        int savedLimit = buffer.limit();

        buffer.position(0);

        DataBuffer destination = pool.allocate(savedLimit > 0 ? savedLimit : 1);
        byte[] tmp = new byte[savedLimit];
        buffer.get(tmp);

        buffer.position(savedPos);
        buffer.limit(savedLimit);

        if (savedLimit > 0) {
            destination.writeBytes(tmp);
            destination.flip();
        }
        return destination;
    }

    @Override
    public ByteBuffer asByteBuffer() {
        checkNotReleased();
        return buffer.duplicate().order(buffer.order());
    }

    @Override
    public DataBuffer retain() {
        int prev = refCount.getAndUpdate(c -> c > 0 ? c + 1 : c);
        if (prev == 0) throw new IllegalStateException("retain() called on released buffer: " + this);
        return this;
    }

    @Override
    public boolean release() {
        int remaining = refCount.decrementAndGet();
        if (remaining < 0) {
            refCount.incrementAndGet();
            throw new IllegalStateException("release() called too many times on buffer: " + this);
        }
        if (remaining == 0) {
            if (parent != null) {
                parent.release();
            } else {
                pool.recycle(this);
            }
            return true;
        }
        return false;
    }

    @Override public boolean isReleased() { return refCount.get() == 0; }
    @Override public int     refCount()   { return refCount.get();       }

    public Slab slab() { return slab; }
    public int chunkIndex() { return chunkIndex; }

    private void checkNotReleased() {
        if (isReleased()) throw new IllegalStateException("Buffer already released: " + this);
    }

    @Override
    public String toString() {
        return "SlabDataBuffer{capacity=" + buffer.capacity()
                + ", refCount=" + refCount.get()
                + ", chunkIndex=" + chunkIndex
                + '}';
    }

}