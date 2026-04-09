package me.bottdev.kern.struct.grid;

public interface MutableGrid<T> extends Grid<T> {

    void set(int row, int column, T value);

    void clear();

    GridFitResult canFit(int originRow, int originColumn, Grid<T> other, boolean override);

    GridFitResult fit(int originRow, int originColumn, Grid<T> other, boolean override);

}
