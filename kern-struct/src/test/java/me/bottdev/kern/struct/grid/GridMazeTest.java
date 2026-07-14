package me.bottdev.kern.struct.grid;

import me.bottdev.kern.struct.Path;
import me.bottdev.kern.struct.algorithms.shortestpath.DijkstraPathFinder;
import me.bottdev.kern.struct.grid.array.ArrayGridBuilder;
import me.bottdev.kern.struct.paths.WeightedPath;
import org.junit.jupiter.api.Test;

import static me.bottdev.kern.struct.grid.GridPathAdapter.NeighborMode.FOUR;
import static org.junit.jupiter.api.Assertions.*;

class GridMazeTest {

    // Лабиринт 7x7, '#' - стена, '.' - проход, 'S' - старт, 'E' - финиш
    //
    //   0123456
    // 0 #######
    // 1 #S....#
    // 2 ###.###
    // 3 #...###
    // 4 #.#####
    // 5 #.....E
    // 6 #######

    private static final Character W = '#';  // стена
    private static final Character P = '.';  // проход
    private static final Character S = 'S';  // старт
    private static final Character E = 'E';  // финиш

    private Grid<Character> buildMaze() {
        return new ArrayGridBuilder<Character>(7, 7)
                .row(0, new Character[]{W, W, W, W, W, W, W})
                .row(1, new Character[]{W, S, P, P, P, P, W})  // <- старт
                .row(2, new Character[]{W, W, W, P, W, W, W})
                .row(3, new Character[]{W, P, P, P, W, W, W})
                .row(4, new Character[]{W, P, W, W, W, W, W})
                .row(5, new Character[]{W, P, P, P, P, P, E})  // <- финиш
                .row(6, new Character[]{W, W, W, W, W, W, W})
                .immutable();
    }

    @Test
    void shortestPath_findsPath() {
        Grid<Character> maze = buildMaze();

        // Стены непроходимы - вес бесконечность, проходы - 1.0
        var adapter = new GridPathAdapter<>(maze, FOUR, (from, to) -> {
            if (to.value() == W) return Double.MAX_VALUE;
            return 1.0;
        });

        GridPosition<Character> start  = findCell(maze, 'S');
        GridPosition<Character> target = findCell(maze, 'E');

        Path<GridPosition<Character>> result =
                new DijkstraPathFinder().find(adapter, start, target);

        assertNotNull(result);
        assertFalse(result.nodes().isEmpty());
        assertEquals(start,  result.nodes().getFirst());
        assertEquals(target, result.nodes().getLast());
    }

    @Test
    void shortestPath_distanceIsCorrect() {
        Grid<Character> maze = buildMaze();

        var adapter = new GridPathAdapter<>(maze, FOUR, (from, to) -> {
            if (to.value() == W) return Double.MAX_VALUE;
            return 1.0;
        });

        GridPosition<Character> start  = findCell(maze, 'S');
        GridPosition<Character> target = findCell(maze, 'E');

        WeightedPath<GridPosition<Character>> result =
                new DijkstraPathFinder().find(adapter, start, target);

        // S(1,1) -> (1,2) -> (1,3) -> (1,4) - нет, там стена (2,4)
        // единственный путь длиной 10 шагов:
        // (1,1)->(1,2)->(1,3)->(2,3)->(3,3)->(3,2)->(3,1)->(4,1)->(5,1)->(5,2)->(5,3)->(5,4)->(5,5)->(5,6)
        assertEquals(13.0, result.distance(), 1e-9);
    }

    @Test
    void shortestPath_nodesDoNotPassThroughWalls() {
        Grid<Character> maze = buildMaze();

        var adapter = new GridPathAdapter<>(maze, FOUR, (from, to) -> {
            if (to.value() == W) return Double.MAX_VALUE;
            return 1.0;
        });

        GridPosition<Character> start  = findCell(maze, 'S');
        GridPosition<Character> target = findCell(maze, 'E');

        WeightedPath<GridPosition<Character>> result =
                new DijkstraPathFinder().find(adapter, start, target);

        result.nodes().forEach(node ->
                assertNotEquals(W, node.value(),
                        "Path goes through wall at (%d, %d)".formatted(node.row(), node.column()))
        );
    }

    @Test
    void shortestPath_pathIsContiguous() {
        Grid<Character> maze = buildMaze();

        var adapter = new GridPathAdapter<>(maze, FOUR, (from, to) -> {
            if (to.value() == W) return Double.MAX_VALUE;
            return 1.0;
        });

        GridPosition<Character> start  = findCell(maze, 'S');
        GridPosition<Character> target = findCell(maze, 'E');

        WeightedPath<GridPosition<Character>> result =
                new DijkstraPathFinder().find(adapter, start, target);

        // каждый следующий узел - сосед предыдущего (расстояние ровно 1 по row или column)
        var nodes = result.nodes();
        for (int i = 0; i < nodes.size() - 1; i++) {
            GridPosition<Character> cur  = nodes.get(i);
            GridPosition<Character> next = nodes.get(i + 1);

            int rowDiff = Math.abs(cur.row()    - next.row());
            int colDiff = Math.abs(cur.column() - next.column());

            assertEquals(1, rowDiff + colDiff,
                    "Non-contiguous step between (%d,%d) and (%d,%d)"
                            .formatted(cur.row(), cur.column(), next.row(), next.column()));
        }
    }

    // ── вспомогательное ───────────────────────────────────────────────────────

    private GridPosition<Character> findCell(Grid<Character> grid, char symbol) {
        for (int row = 0; row < grid.height(); row++) {
            for (int col = 0; col < grid.width(); col++) {
                if (grid.get(row, col) == symbol) {
                    return new GridPosition<>(row, col, symbol);
                }
            }
        }
        throw new IllegalStateException("Symbol '%c' not found in maze".formatted(symbol));
    }

}