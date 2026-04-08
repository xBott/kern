package me.bottdev.kern.struct.matrix;

import lombok.Getter;

import java.lang.reflect.Array;

public abstract class Matrix<T> {

    @Getter
    protected final int rows;
    @Getter
    protected final int columns;
    protected final T[][] data;

    @SuppressWarnings("unchecked")
    public Matrix(Class<T> clazz, int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.data = (T[][]) Array.newInstance(clazz, rows, columns);
    }

    public T get(int row, int column) {
        return data[row][column];
    }

    public T getOrDefault(int row, int column, T defaultValue) {
        return data[row][column] != null ? get(row, column) : defaultValue;
    }

    public void set(int row, int column, T value) {
        data[row][column] = value;
    }

    public boolean hasSameSize(Matrix<T> other) {
        return this.rows == other.rows && this.columns == other.columns;
    }

    public void print() {
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                System.out.print(get(row, column) + " ");
            }
            System.out.print("\n");
        }
    }

}
