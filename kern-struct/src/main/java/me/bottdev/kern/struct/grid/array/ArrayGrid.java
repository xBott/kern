package me.bottdev.kern.struct.grid.array;

import me.bottdev.kern.struct.grid.Grid;
import me.bottdev.kern.struct.grid.GridPosition;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class ArrayGrid<T> implements Grid<T> {

    protected final int width;
    protected final int height;
    protected final T[][] data;

    @SuppressWarnings("unchecked")
    public ArrayGrid(int width, int height) {
        this.width = width;
        this.height = height;
        this.data = (T[][]) new Object[height][width];
    }

    @SuppressWarnings("unchecked")
    public ArrayGrid(T[][] source) {
        this.height = source.length;
        this.width  = source.length > 0 ? source[0].length : 0;
        this.data   = (T[][]) new Object[height][width];
        for (int row = 0; row < height; row++) {
            System.arraycopy(source[row], 0, data[row], 0, width);
        }
    }

    @Override
    public int width() {
        return width;
    }

    @Override
    public int height() {
        return height;
    }

    @Override
    public T get(int row, int column) {
        if (!inBounds(row, column)) throw new IndexOutOfBoundsException(
                "(%d, %d) out of bounds for %dx%d grid".formatted(row, column, width, height)
        );
        return data[row][column];
    }

    @Override
    public Optional<T> find(int row, int column) {
        return inBounds(row, column) ? Optional.ofNullable(data[row][column]) : Optional.empty();
    }

    private Set<GridPosition<T>> collectNeighbors(int row, int column, int[] dRow, int[] dColumn) {
        Set<GridPosition<T>> result = new HashSet<>();
        for (int i = 0; i < dRow.length; i++) {
            int nRow = row + dRow[i];
            int nColumn = column + dColumn[i];
            if (inBounds(nRow, nColumn)) {
                result.add(new GridPosition<>(nRow, nColumn, data[nRow][nColumn]));
            }
        }
        return result;
    }

    @Override
    public Set<GridPosition<T>> neighbors4(int row, int column) {
        return collectNeighbors(
                row, column,
                new int[]{0, 0, -1, 1},
                new int[]{-1, 1, 0, 0}
        );
    }

    @Override
    public Set<GridPosition<T>> neighbors8(int row, int column) {
        return collectNeighbors(
                row, column,
                new int[]{-1, 0, 1, -1, 1, -1, 0, 1},
                new int[]{-1, -1, -1, 0, 0, 1, 1, 1}
        );
    }


    @SuppressWarnings("unchecked")
    @Override
    public ArrayGrid<T> immutableCopy() {

        T[][] copyData = (T[][]) new Object[height][width];
        for (int row = 0; row < height; row++) {
            System.arraycopy(data[row], 0, copyData[row], 0, width);
        }

        return new ArrayGrid<>(copyData);

    }

    @SuppressWarnings("unchecked")
    @Override
    public MutableArrayGrid<T> mutableCopy() {

        T[][] copyData = (T[][]) new Object[height][width];
        for (int row = 0; row < height; row++) {
            System.arraycopy(data[row], 0, copyData[row], 0, width);
        }

        return new MutableArrayGrid<>(copyData);

    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                sb.append(data[row][column] == null ? "." : data[row][column]);
                if (column < width - 1) sb.append(' ');
            }
            sb.append('\n');
        }
        return sb.toString();
    }

}
