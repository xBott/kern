package me.bottdev.kern.memory;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class SlabTest {

    private static final int CHUNK_SIZE = 128;
    private static final int SLAB_CHUNKS = 8;

    private Slab slab;

    @BeforeEach
    public void setup() {
        int bytes = CHUNK_SIZE * SLAB_CHUNKS;
        ByteBuffer memory = ByteBuffer.allocateDirect(bytes);
        slab = new Slab(memory, CHUNK_SIZE);
    }

    @Test
    void allocateIndex_once() {

        int index = slab.allocateIndex();
        assertThat(index).isGreaterThanOrEqualTo(0);
        assertEquals(1, slab.usedChunks());

    }

    @Test
    void allocateIndex_multiple() {


        int iterations = 6;
        int[] indices = new int[iterations];
        int[] expected = IntStream.range(0, iterations).toArray();

        for (int i = 0; i < iterations; i++) {
            indices[i] = slab.allocateIndex();
        }

        assertThat(indices)
                .doesNotHaveDuplicates()
                .containsExactlyInAnyOrder(expected);
        assertEquals(6, slab.usedChunks());
        assertFalse(slab.isFull());

    }

    @Test
    void allocateIndex_multiple_fullyOccupied() {

        int iterations = 16;
        int[] indices = new int[iterations];

        for (int i = 0; i < iterations; i++) {
            indices[i] = slab.allocateIndex();
        }

        assertThat(indices).contains(-1);
        assertEquals(SLAB_CHUNKS, slab.usedChunks());
        assertTrue(slab.isFull());

    }

    @Test
    void free_single() {

        int index = slab.allocateIndex();
        boolean isEmpty = slab.isEmpty();

        slab.free(index);

        assertFalse(isEmpty);
        assertTrue(slab.isEmpty());

    }

    @Test
    void free_multiple() {

        for (int i = 0; i < SLAB_CHUNKS; i++) {
            slab.allocateIndex();
        }
        boolean isEmpty = slab.isEmpty();

        int[] freeIndices = new int[] {7, 2, 5, 4, 1, 3, 6, 0};
        for (int freeIndex : freeIndices) {
            slab.free(freeIndex);
        }

        assertFalse(isEmpty);
        assertTrue(slab.isEmpty());

    }

    @Test
    void free_andAllocateAgain() {

        slab.allocateIndex();
        int secondIndexBefore = slab.allocateIndex();

        slab.free(secondIndexBefore);
        int secondIndexAfter = slab.allocateIndex();

        assertEquals(secondIndexBefore, secondIndexAfter);
        assertEquals(2, slab.usedChunks());

    }

    @Test
    void free_double() {

        slab.allocateIndex();

        assertDoesNotThrow(() -> slab.free(0));
        assertThrows(IllegalStateException.class, () -> slab.free(0));

    }

    @Test
    void free_incorrectIndex() {

        slab.allocateIndex();
        assertThrows(IllegalArgumentException.class, () -> slab.free(-100));
        assertThrows(IllegalArgumentException.class, () -> slab.free(-1));
        assertThrows(IllegalArgumentException.class, () -> slab.free(8));
        assertThrows(IllegalArgumentException.class, () -> slab.free(20));
        assertDoesNotThrow(() -> slab.free(0));

    }

    @Test
    void slice_single() {
        int index = slab.allocateIndex();
        ByteBuffer slice = slab.slice(index);
        assertEquals(CHUNK_SIZE, slice.capacity());
    }

    @Test
    void slice_multiple() {

        int index;
        int iterations = SLAB_CHUNKS;
        ByteBuffer[] slices = new ByteBuffer[iterations];

        for (int i = 0; i < iterations; i++) {
            index = slab.allocateIndex();
            slices[i] = slab.slice(index);
        }

        assertThat(slices)
                .allSatisfy(slice -> assertThat(slice.capacity()).isEqualTo(CHUNK_SIZE));

    }

    @Test
    void slice_fullyOccupied() {

        int index;
        int iterations = SLAB_CHUNKS;
        ByteBuffer[] slices = new ByteBuffer[iterations];

        for (int i = 0; i < iterations; i++) {
            index = slab.allocateIndex();
            slices[i] = slab.slice(index);
        }

        int incorrectIndex = slab.allocateIndex();

        assertThat(slices)
                .allSatisfy(slice -> assertThat(slice.capacity()).isEqualTo(CHUNK_SIZE));
        assertEquals(-1, incorrectIndex);
        assertThrows(IllegalArgumentException.class, () -> slab.slice(incorrectIndex));

    }

    @Test
    void slice_incorrectIndex() {

        slab.allocateIndex();

        assertThrows(IllegalArgumentException.class, () -> slab.slice(-100));
        assertThrows(IllegalArgumentException.class, () -> slab.slice(-1));
        assertThrows(IllegalArgumentException.class, () -> slab.slice(10));
        assertThrows(IllegalArgumentException.class, () -> slab.slice(200));
        assertDoesNotThrow(() -> slab.slice(0));

    }

    @Test
    void toString_containsUsefulInfo() {
        Assertions.assertThat(slab.toString())
                .contains("Slab")
                .contains("chunkSize=" + CHUNK_SIZE)
                .contains("capacity=" + SLAB_CHUNKS)
                .contains("used=0");
    }

}
