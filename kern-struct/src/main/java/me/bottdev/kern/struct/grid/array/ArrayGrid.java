package me.bottdev.kern.struct.grid.array;

import lombok.Getter;
import me.bottdev.kern.struct.grid.Grid;
import me.bottdev.kern.struct.grid.GridBuilder;
import me.bottdev.kern.struct.grid.GridPosition;
import me.bottdev.kern.struct.matrix.DoubleMatrix;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class ArrayGrid<T> implements Grid<T> {

    protected double angle;
    protected final int width;
    protected final int height;

    @Getter
    protected final T[][] data;

    @SuppressWarnings("unchecked")
    public ArrayGrid(int width, int height, double angle) {
        this.width = width;
        this.height = height;
        this.angle = angle;
        this.data = (T[][]) new Object[height][width];
    }

    @SuppressWarnings("unchecked")
    public ArrayGrid(T[][] source, double angle) {
        this.height = source.length;
        this.width  = source.length > 0 ? source[0].length : 0;
        this.angle = angle;
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
    public double angle() {
        return angle;
    }

    @Override
    public T get(int row, int column) {
        if (!inBounds(row, column)) throw new IndexOutOfBoundsException(
                "(%d, %d) out of bounds for %dx%d grid".formatted(row, column, width, height)
        );
        return data[row][column];
    }

    @Override
    public GridPosition<T> positioned(int row, int column) {
        T value = get(row, column);
        return new GridPosition<>(row, column, value);
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

        return new ArrayGrid<>(copyData, angle);

    }

    @SuppressWarnings("unchecked")
    @Override
    public MutableArrayGrid<T> mutableCopy() {

        T[][] copyData = (T[][]) new Object[height][width];
        for (int row = 0; row < height; row++) {
            System.arraycopy(data[row], 0, copyData[row], 0, width);
        }

        return new MutableArrayGrid<>(copyData, angle);

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

    @Override
    public GridBuilder<T> rotate(double degrees) {

        angle = (angle + degrees) % 360;

        DoubleMatrix rotationMatrix = DoubleMatrix.getRotationMatrix(degrees);

        double centerRow = (height - 1) / 2.0;
        double centerColumn = (width - 1) / 2.0;

        Grid<T> snapshot = immutableCopy();

        double minRow = Double.MAX_VALUE;
        double maxRow = Double.MIN_VALUE;
        double minCol = Double.MAX_VALUE;
        double maxCol = Double.MIN_VALUE;

        for (int row = 0; row < snapshot.height(); row++) {
            for (int col = 0; col < snapshot.width(); col++) {

                if (snapshot.get(row, col) == null) continue;

                double relRow = row - centerRow;
                double relCol = col - centerColumn;

                DoubleMatrix point = new DoubleMatrix(1, 2)
                        .setRow(0, new Double[]{relRow, relCol});

                DoubleMatrix rotated = point.multiply(rotationMatrix);

                double r = rotated.get(0, 0);
                double c = rotated.get(0, 1);

                minRow = Math.min(minRow, r);
                maxRow = Math.max(maxRow, r);
                minCol = Math.min(minCol, c);
                maxCol = Math.max(maxCol, c);
            }
        }

        int newHeight = (int) Math.ceil(maxRow - minRow + 1);
        int newWidth  = (int) Math.ceil(maxCol - minCol + 1);

        ArrayGridBuilder<T> builder = new ArrayGridBuilder<T>(newWidth, newHeight);

        double offsetRow = -minRow;
        double offsetCol = -minCol;

        for (int row = 0; row < snapshot.height(); row++) {
            for (int col = 0; col < snapshot.width(); col++) {

                T value = snapshot.get(row, col);
                if (value == null) continue;

                double relRow = row - centerRow;
                double relCol = col - centerColumn;

                DoubleMatrix point = new DoubleMatrix(1, 2)
                        .setRow(0, new Double[]{relRow, relCol});

                DoubleMatrix rotated = point.multiply(rotationMatrix);

                int newRow = (int) Math.round(rotated.get(0, 0) + offsetRow);
                int newCol = (int) Math.round(rotated.get(0, 1) + offsetCol);

                builder.cell(newRow, newCol, value);

            }
        }

        return builder;
    }

}
