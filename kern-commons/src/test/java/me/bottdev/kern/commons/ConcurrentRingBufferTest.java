package me.bottdev.kern.commons;

import me.bottdev.kern.commons.buffer.ConcurrentRingBuffer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ConcurrentRingBufferTest {

    private ConcurrentRingBuffer<Integer> buffer;

    @BeforeEach
    void setUp() {
        buffer = new ConcurrentRingBuffer<>(4); // Capacity must be power of two
    }

    @Test
    @DisplayName("Constructor: throws exception for invalid capacity")
    void shouldThrowIfCapacityIsInvalid() {
        assertThrows(IllegalArgumentException.class, () -> new ConcurrentRingBuffer<>(0));
        assertThrows(IllegalArgumentException.class, () -> new ConcurrentRingBuffer<>(-1));
        assertThrows(IllegalArgumentException.class, () -> new ConcurrentRingBuffer<>(3)); // not power of two
    }

    @Test
    @DisplayName("size and isEmpty: returns correct values initially")
    void shouldReturnCorrectInitialSize() {
        assertTrue(buffer.isEmpty());
        assertEquals(0, buffer.size());
        assertEquals(4, buffer.capacity());
        assertFalse(buffer.isFull());
    }

    @Test
    @DisplayName("add: increases size up to capacity and implements Collection methods")
    void shouldAddElementsUpToCapacity() {
        assertTrue(buffer.add(1));
        buffer.add(2);

        assertFalse(buffer.isEmpty());
        assertEquals(2, buffer.size());
        assertTrue(buffer.contains(1));

        List<Integer> snapshot = buffer.snapshot();
        assertThat(snapshot).containsExactly(1, 2);
    }

    @Test
    @DisplayName("add: overwrites oldest elements when full")
    void shouldOverwriteOldestElementsWhenFull() {
        buffer.add(1);
        buffer.add(2);
        buffer.add(3);
        buffer.add(4); // buffer is now full

        assertTrue(buffer.isFull());
        assertEquals(4, buffer.size());
        assertThat(buffer.snapshot()).containsExactly(1, 2, 3, 4);

        buffer.add(5); // overwrites 1
        buffer.add(6); // overwrites 2

        assertEquals(4, buffer.size());
        assertThat(buffer.snapshot()).containsExactly(3, 4, 5, 6);
    }

    @Test
    @DisplayName("forEach: iterates correctly over elements using Iterable interface")
    void shouldIterateUsingForEach() {
        buffer.add(1);
        buffer.add(2);
        buffer.add(3);
        buffer.add(4);
        buffer.add(5); // overwrites 1

        List<Integer> iterated = new ArrayList<>();
        buffer.forEach(iterated::add);

        assertThat(iterated).containsExactly(2, 3, 4, 5);
        
        // Also test stream API because it implements Collection
        long count = buffer.stream().filter(i -> i > 3).count();
        assertEquals(2, count);
    }

    @Test
    @DisplayName("clear: resets the buffer")
    void shouldClearBuffer() {
        buffer.add(1);
        buffer.add(2);

        buffer.clear();

        assertTrue(buffer.isEmpty());
        assertEquals(0, buffer.size());
        assertThat(buffer.snapshot()).isEmpty();

        // Ensure we can add again after clear
        buffer.add(3);
        assertEquals(1, buffer.size());
        assertThat(buffer.snapshot()).containsExactly(3);
    }

    @Test
    @DisplayName("Concurrency: thread-safe add and snapshot")
    void shouldBeThreadSafeUnderLoad() throws InterruptedException {
        int capacity = 1024;
        ConcurrentRingBuffer<Integer> concurrentBuffer = new ConcurrentRingBuffer<>(capacity);
        
        int threads = 10;
        int elementsPerThread = 1000;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);

        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                for (int j = 0; j < elementsPerThread; j++) {
                    concurrentBuffer.add(j);
                }
                latch.countDown();
            });
        }

        assertTrue(latch.await(5, TimeUnit.SECONDS));
        executor.shutdown();

        // The buffer should have exactly 'capacity' elements, as total added elements > capacity
        assertEquals(capacity, concurrentBuffer.size());
        
        // Ensure snapshot does not throw any exceptions and returns expected amount of elements
        List<Integer> snapshot = concurrentBuffer.snapshot();
        assertEquals(capacity, snapshot.size());
        assertFalse(snapshot.contains(null));
    }
}
