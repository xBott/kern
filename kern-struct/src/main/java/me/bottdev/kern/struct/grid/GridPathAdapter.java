package me.bottdev.kern.struct.grid;

import me.bottdev.kern.struct.PathStructure;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiFunction;

public class GridPathAdapter<T> implements PathStructure<GridPosition<T>> {

    public enum NeighborMode { FOUR, EIGHT }

    private static final double STRAIGHT  = 1.0;
    private static final double DIAGONAL  = Math.sqrt(2);

    private final Grid<T> grid;
    private final NeighborMode mode;
    private final BiFunction<GridPosition<T>, GridPosition<T>, Double> weightFunction;

    public GridPathAdapter(Grid<T> grid, NeighborMode mode) {
        this.grid = grid;
        this.mode = mode;
        this.weightFunction = GridPathAdapter::defaultWeight;
    }

    public GridPathAdapter(Grid<T> grid, NeighborMode mode,
                           BiFunction<GridPosition<T>, GridPosition<T>, Double> weightFunction) {
        this.grid = grid;
        this.mode = mode;
        this.weightFunction = weightFunction;
    }

    @Override
    public Set<GridPosition<T>> elements() {
        Set<GridPosition<T>> result = new HashSet<>();
        for (int row = 0; row < grid.height(); row++) {
            for (int col = 0; col < grid.width(); col++) {
                result.add(new GridPosition<>(row, col, grid.get(row, col)));
            }
        }
        return result;
    }

    @Override
    public Iterable<GridPosition<T>> neighbors(GridPosition<T> node) {
        return mode == NeighborMode.FOUR
                ? grid.neighbors4(node.row(), node.column())
                : grid.neighbors8(node.row(), node.column());
    }

    @Override
    public double weightBetween(GridPosition<T> from, GridPosition<T> to) {
        return weightFunction.apply(from, to);
    }

    private static <T> double defaultWeight(GridPosition<T> from, GridPosition<T> to) {
        boolean diagonal = from.row() != to.row() && from.column() != to.column();
        return diagonal ? DIAGONAL : STRAIGHT;
    }

}