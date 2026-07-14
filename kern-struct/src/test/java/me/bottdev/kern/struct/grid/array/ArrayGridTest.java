package me.bottdev.kern.struct.grid.array;

import me.bottdev.kern.struct.grid.GridPosition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ArrayGridTest {

    private ArrayGrid<Character> grid;

    @BeforeEach
    public void setUp() {
        grid = new ArrayGridBuilder<Character>(3, 3)
                .row(0, new Character[] { null, 'N', null })
                .row(1, new Character[] { 'W', '+', 'E' })
                .row(2, new Character[] { null, 'S', null })
                .immutable();
    }

    @Test
    void width() {
        assertEquals(3, grid.width());
    }

    @Test
    void height() {
        assertEquals(3, grid.height());
    }

    @Test
    void get_returnsValue() {
        assertEquals('N', grid.get(0, 1));
        assertEquals('W', grid.get(1, 0));
        assertEquals('+', grid.get(1, 1));
        assertEquals('E', grid.get(1, 2));
        assertEquals('S', grid.get(2, 1));
    }

    @Test
    void get_returnsNull_whenEmpty() {
        assertNull(grid.get(0, 0));
    }

    @Test
    void get_throwsException_whenOutOfBounds() {
        assertThrows(IndexOutOfBoundsException.class, () -> grid.get(-1, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> grid.get(0, 99));
    }

    @Test
    void find_returnsValue_whenPresent() {
        assertEquals(Optional.of('W'), grid.find(1, 0));
    }

    @Test
    void find_returnsEmpty_whenNull() {
        assertEquals(Optional.empty(), grid.find(0, 0));
    }

    @Test
    void find_returnsEmpty_whenOutOfBounds() {
        assertEquals(Optional.empty(), grid.find(-1, 0));
        assertEquals(Optional.empty(), grid.find(99, 99));
    }

    @Test
    void neighbors4_centerCell() {
        Set<GridPosition<Character>> neighbors = grid.neighbors4(1, 1);

        assertEquals(4, neighbors.size());
        assertTrue(neighbors.stream().anyMatch(p -> p.row() == 1 && p.column() == 0 && p.value() == 'W'));
        assertTrue(neighbors.stream().anyMatch(p -> p.row() == 0 && p.column() == 1 && p.value() == 'N'));
        assertTrue(neighbors.stream().anyMatch(p -> p.row() == 2 && p.column() == 1 && p.value() == 'S'));
        assertTrue(neighbors.stream().anyMatch(p -> p.row() == 1 && p.column() == 2 && p.value() == 'E'));
    }

    @Test
    void neighbors4_cornerCell() {
        Set<GridPosition<Character>> neighbors = grid.neighbors4(0, 0);
        assertEquals(2, neighbors.size());
    }

    @Test
    void neighbors4_edgeCell() {
        Set<GridPosition<Character>> neighbors = grid.neighbors4(1, 0);
        assertEquals(3, neighbors.size());
    }

    @Test
    void neighbors8_centerCell() {
        Set<GridPosition<Character>> neighbors = grid.neighbors8(1, 1);
        assertEquals(8, neighbors.size());
    }

    @Test
    void neighbors8_cornerCell() {
        Set<GridPosition<Character>> neighbors = grid.neighbors8(0, 0);
        assertEquals(3, neighbors.size());
    }

    @Test
    void neighbors8_edgeCell() {
        Set<GridPosition<Character>> neighbors = grid.neighbors8(1, 0);
        assertEquals(5, neighbors.size());
    }

    @Test
    void copy_isEqualToOriginal() {
        var copy = grid.copy();

        assertEquals(grid.width(), copy.width());
        assertEquals(grid.height(), copy.height());

        for (int x = 0; x < grid.width(); x++) {
            for (int y = 0; y < grid.height(); y++) {
                assertEquals(grid.get(x, y), copy.get(x, y));
            }
        }
    }

}