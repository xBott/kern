package me.bottdev.kern.struct.grid;

import me.bottdev.kern.struct.algorithms.shortestpath.Dijkstra;
import me.bottdev.kern.struct.grid.array.ArrayGridBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static me.bottdev.kern.struct.grid.GridPathAdapter.NeighborMode.*;
import static org.junit.jupiter.api.Assertions.*;

class GridPathAdapterTest {

    private Grid<Character> grid;
    private Dijkstra dijkstra = new Dijkstra();

    @BeforeEach
    void setUp() {
        grid = new ArrayGridBuilder<Character>(3, 3)
                .row(0, new Character[]{null, 'N', null})
                .row(1, new Character[]{'W',  '+', 'E'})
                .row(2, new Character[]{null, 'S', null})
                .immutable();
    }

    @Test
    void elements_containsAllCells() {
        var adapter = new GridPathAdapter<>(grid, FOUR);
        assertEquals(9, adapter.elements().size());
    }

    @Test
    void elements_valuesMatchGrid() {
        var adapter = new GridPathAdapter<>(grid, FOUR);
        Set<GridPosition<Character>> elements = adapter.elements();

        assertTrue(elements.stream().anyMatch(p -> p.row() == 0 && p.column() == 1 && p.value() == 'N'));
        assertTrue(elements.stream().anyMatch(p -> p.row() == 1 && p.column() == 1 && p.value() == '+'));
        assertTrue(elements.stream().anyMatch(p -> p.row() == 0 && p.column() == 0 && p.value() == null));
    }

    // ── neighbors FOUR ────────────────────────────────────────────────────────

    @Test
    void neighbors4_centerHas4Neighbors() {
        var adapter = new GridPathAdapter<>(grid, FOUR);
        var center  = new GridPosition<>(1, 1, '+');

        long count = countNeighbors(adapter, center);
        assertEquals(4, count);
    }

    @Test
    void neighbors4_cornerHas2Neighbors() {
        var adapter  = new GridPathAdapter<>(grid, FOUR);
        var corner   = new GridPosition<Character>(0, 0, null);

        long count = countNeighbors(adapter, corner);
        assertEquals(2, count);
    }

    @Test
    void neighbors4_edgeHas3Neighbors() {
        var adapter = new GridPathAdapter<>(grid, FOUR);
        var edge    = new GridPosition<Character>(0, 1, 'N');

        long count = countNeighbors(adapter, edge);
        assertEquals(3, count);
    }

    // ── neighbors EIGHT ───────────────────────────────────────────────────────

    @Test
    void neighbors8_centerHas8Neighbors() {
        var adapter = new GridPathAdapter<>(grid, EIGHT);
        var center  = new GridPosition<Character>(1, 1, '+');

        long count = countNeighbors(adapter, center);
        assertEquals(8, count);
    }

    @Test
    void neighbors8_cornerHas3Neighbors() {
        var adapter = new GridPathAdapter<>(grid, EIGHT);
        var corner  = new GridPosition<Character>(0, 0, null);

        long count = countNeighbors(adapter, corner);
        assertEquals(3, count);
    }

    // ── weightBetween ─────────────────────────────────────────────────────────

    @Test
    void weight_straightNeighborIs1() {
        var adapter = new GridPathAdapter<>(grid, FOUR);
        var from    = new GridPosition<Character>(1, 1, '+');
        var to      = new GridPosition<Character>(0, 1, 'N');

        assertEquals(1.0, adapter.weightBetween(from, to));
    }

    @Test
    void weight_diagonalNeighborIsSqrt2() {
        var adapter = new GridPathAdapter<>(grid, EIGHT);
        var from    = new GridPosition<Character>(1, 1, '+');
        var to      = new GridPosition<Character>(0, 0, null);

        assertEquals(Math.sqrt(2), adapter.weightBetween(from, to), 1e-9);
    }

    @Test
    void weight_customFunction() {
        // вес = длина пути * 10 (просто для теста что функция применяется)
        var adapter = new GridPathAdapter<>(grid, FOUR,
                (from, to) -> 10.0);

        var from = new GridPosition<Character>(1, 1, '+');
        var to   = new GridPosition<Character>(0, 1, 'N');

        assertEquals(10.0, adapter.weightBetween(from, to));
    }

    // ── вспомогательное ───────────────────────────────────────────────────────

    private long countNeighbors(GridPathAdapter<Character> adapter, GridPosition<Character> node) {
        long count = 0;
        for (var ignored : adapter.neighbors(node)) count++;
        return count;
    }

}