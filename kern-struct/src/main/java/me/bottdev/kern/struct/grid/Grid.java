package me.bottdev.kern.struct.grid;

import java.util.Optional;
import java.util.Set;

public interface Grid<T> {

    int width();
    int height();

    T get(int row, int column);
    Optional<T> find(int row, int column);

    default boolean inBounds(int row, int column) {
        return row >= 0 && column >= 0 && row < height() && column < width();
    }

    Set<GridPosition<T>> neighbors4(int row, int column);
    Set<GridPosition<T>> neighbors8(int row, int column);

    Grid<T> immutableCopy();

    MutableGrid<T> mutableCopy();

}
