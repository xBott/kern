package me.bottdev.kern.struct.grid;

public interface GridBuilder<T> {

    Grid<T> immutable();
    MutableGrid<T> mutable();

}
