package me.bottdev.kern.struct.grid.array;

import me.bottdev.kern.struct.grid.GridBuilder;

import java.util.Arrays;

public class ArrayGridBuilder<T> implements GridBuilder<T> {

    private final T[][] data;
    private final int width;
    private final int height;

    @SuppressWarnings("unchecked")
    public ArrayGridBuilder(int width, int height) {
        int h = Math.max(0, height);
        int w = Math.max(0, width);
        this.data = (T[][]) new Object[h][w];
        this.height = h;
        this.width = w;
    }

    public ArrayGridBuilder(T[][] source) {
        this.data = source;
        this.height = (source != null) ? source.length : 0;
        this.width = (height > 0 && source[0] != null) ? source[0].length : 0;
    }

    public ArrayGridBuilder<T> cell(int row, int column, T value) {
        if (row < 0 || row >= height || column < 0 || column >= width) {
            return this;
        }
        data[row][column] = value;
        return this;
    }

    public ArrayGridBuilder<T> row(int row, T[] values) {
        if (values == null || row < 0 || row >= height) {
            return this;
        }

        int lengthToCopy = Math.min(values.length, width);
        System.arraycopy(values, 0, data[row], 0, lengthToCopy);
        return this;
    }

    public ArrayGridBuilder<T> column(int column, T[] values) {
        if (values == null || column < 0 || column >= width) {
            return this;
        }

        int lengthToFill = Math.min(values.length, height);
        for (int row = 0; row < lengthToFill; row++) {
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
        return new ArrayGrid<>(data, 0);
    }

    @Override
    public MutableArrayGrid<T> mutable() {
        return new MutableArrayGrid<>(data, 0);
    }

}