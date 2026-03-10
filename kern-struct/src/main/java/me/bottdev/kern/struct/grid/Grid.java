package me.bottdev.kern.struct.grid;

import java.util.Optional;
import java.util.Set;

public interface Grid<T> {

    int width();
    int height();

    T get(int x, int y);
    Optional<T> find(int x, int y);

    default boolean inBounds(int x, int y) {
        return x >= 0 && y >= 0 && x < width() && y < height();
    }

    Set<GridPosition<T>> neighbors4(int x, int y);
    Set<GridPosition<T>> neighbors8(int x, int y);

}
