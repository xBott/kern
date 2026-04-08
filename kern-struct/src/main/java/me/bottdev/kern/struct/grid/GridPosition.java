package me.bottdev.kern.struct.grid;

import java.util.Objects;

public record GridPosition<T>(
        int row,
        int column,
        T value
) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GridPosition<?>(int row1, int column1, Object value1))) return false;
        return row == row1
                && column == column1
                && Objects.equals(value, value1);
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, column, value);
    }

    @Override
    public String toString() {
        return "GridPosition(%d, %d, %s)".formatted(row, column, value);
    }

}
