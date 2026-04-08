package me.bottdev.kern.struct.grid;

public interface GridBuilder<T> {

    Grid<T> immutable();
    Grid<T> mutable();

}
