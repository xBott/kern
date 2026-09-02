package me.bottdev.kern.memory;

import java.nio.ByteBuffer;

/// Data Buffer with reference counter.
///
/// Lifecycle: every `allocate` returns a buffer with refCount=1.
/// [#retain()] call increments counter, [#release()] — decrements.
/// If counter reaches 0, it is released to the pool automatically.
///
/// Implementations must not be thread-safe on the level of reading and writing —
/// thread coordination is the responsibility of the calling code..
public interface DataBuffer {


    /// Amount of bytes available for reading (limit − position).
    int readableBytes();

    /// Amount of bytes available for writing (capacity − limit).
    int writableBytes();

    /// Full capacity of the buffer in bytes.
    int capacity();


    byte readByte();
    short readShort();
    int readInt();
    long readLong();

    /// Reads `dst.length` bytes to array.
    void readBytes(byte[] dst);

    /// Reads `length` bytes starting from `offset` to array.
    void readBytes(byte[] dst, int offset, int length);


    DataBuffer writeByte(byte value);
    DataBuffer writeShort(short value);
    DataBuffer writeInt(int value);
    DataBuffer writeLong(long value);

    /// Writes all bytes from the array.
    DataBuffer writeBytes(byte[] src);

    /// Writes `length` bytes from array starting from `offset`.
    DataBuffer writeBytes(byte[] src, int offset, int length);

    byte  getByte(int index);
    short getShort(int index);
    int   getInt(int index);
    long  getLong(int index);

    void setByte(int index, byte value);
    void setShort(int index, short value);
    void setInt(int index, int value);
    void setLong(int index, long value);

    /// Current position of reading/writing.
    int position();

    /// Moves the position to the specified position.
    DataBuffer position(int newPosition);

    /// Resets position=0, limit=capacity.
    DataBuffer clear();

    /// Switches buffer to reading mode: limit=position, position=0.
    DataBuffer flip();

    /// Returns a slice without copying the data.
    /// The slice shares memory with the source buffer; the source’s refCount is incremented.
    DataBuffer slice(int index, int length);

    /// Returns an independent copy of the content.
    /// The copy is allocated from the same pool.
    DataBuffer copy();


    /// Provides a direct access to [ByteBuffer].
    /// Change of position/limit of returned object does not affect the internal state.
    ByteBuffer asByteBuffer();


    /// Increment reference counter. Returns `this` for convenience.
    DataBuffer retain();

    /// Decrements reference counter. If it reaches 0, buffer is released to the pool.
    ///
    /// @return `true`, if buffer was released (refCount reached 0)
    boolean release();

    /// @return `true`, if buffer is already released (refCount == 0).
    boolean isReleased();

    /// Current value of reference counter. Used for debug.
    int refCount();

}