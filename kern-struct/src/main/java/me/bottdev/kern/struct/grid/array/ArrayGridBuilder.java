package me.bottdev.kern.struct.grid.array;

import me.bottdev.kern.struct.grid.GridBuilder;

import java.util.Arrays;

public class ArrayGridBuilder<T> implements GridBuilder<T> {

    private final T[][] data;

    @SuppressWarnings("unchecked")
    public ArrayGridBuilder(int width, int height) {
        this.data = (T[][]) new Object[height][width];
    }

    public ArrayGridBuilder(T[][] source) {
        this.data = source;
    }

    public ArrayGridBuilder<T> cell(int row, int column, T value) {
        data[row][column] = value;
        return this;
    }

    public ArrayGridBuilder<T> row(int row, T[] values) {
        System.arraycopy(values, 0, data[row], 0, values.length);
        return this;
    }

    public ArrayGridBuilder<T> column(int column, T[] values) {
        for (int row = 0; row < values.length; row++) {
            data[row][column] = values[row];
        }
        return this;
    }

    public ArrayGridBuilder<T> fill(T value) {
        for (T[] datum : data) {
            Arrays.fill(datum, value);
        }
        return this;
    }

    @Override
    public ArrayGrid<T> immutable() {
        return new ArrayGrid<>(data);
    }

    @Override
    public MutableArrayGrid<T> mutable() {
        return new MutableArrayGrid<>(data);
    }

}