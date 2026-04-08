package me.bottdev.kern.struct.grid.array;

import me.bottdev.kern.struct.grid.Grid;
import me.bottdev.kern.struct.grid.GridFitResult;
import me.bottdev.kern.struct.grid.MutableGrid;
import me.bottdev.kern.struct.matrix.DoubleMatrix;

public class MutableArrayGrid<T> extends ArrayGrid<T> implements MutableGrid<T> {

    private double angle = 0;

    public MutableArrayGrid(int width, int height) {
        super(width, height);
    }

    public MutableArrayGrid(T[][] source) {
        super(source);
    }


    @Override
    public double angle() {
        return angle;
    }

    @Override
    public void set(int row, int column, T value) {
        data[row][column] = value;
    }

    @Override
    public void clear() {
        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                set(row, column, null);
            }
        }
    }

    @Override
    public GridFitResult canFit(int originRow, int originColumn, Grid<T> other, boolean override) {

        if (originRow + other.height() > height || originColumn + other.width() > width) return GridFitResult.OUT_OF_BOUNDS;

        for (int row = originRow; row < originRow + other.height(); row++) {
            for (int column = originColumn; column < originColumn + other.width(); column++) {

                if (get(row, column) != null && !override) return GridFitResult.INTERSECTS;

            }
        }

        return GridFitResult.FIT;
    }

    @Override
    public GridFitResult fit(int originRow, int originColumn, Grid<T> other, boolean override) {

        GridFitResult result = canFit(originRow, originColumn, other, override);
        if (result != GridFitResult.FIT) return result;

        for (int row = 0; row < other.height(); row++) {
            for (int column = 0; column < other.width(); column++) {

                T element = other.get(row, column);
                set(originRow + row, originColumn + column, element);

            }
        }

        return GridFitResult.FIT;
    }

    @Override
    public void rotate(double degrees) {

        angle = (angle + degrees) % 360;
        DoubleMatrix rotationMatrix = DoubleMatrix.getRotationMatrix(degrees);

        double centerRow = (height - 1) / 2.0;
        double centerColumn = (width - 1) / 2.0;

        Grid<T> snapshot = immutableCopy();

        clear();

        for (int row = 0; row < snapshot.height(); row++) {
            for (int column = 0; column < snapshot.width(); column++) {

                T value = snapshot.get(row, column);
                if (value == null) continue;

                double relativeRow = row - centerRow;
                double relativeColumn = column - centerColumn;

                DoubleMatrix point = new DoubleMatrix(1, 2)
                        .setRow(0, new Double[]{relativeRow, relativeColumn});

                DoubleMatrix rotated = point.multiply(rotationMatrix);

                int newRow = (int) Math.round(rotated.get(0, 0) + centerRow);
                int newColumn = (int) Math.round(rotated.get(0, 1) + centerColumn);

                if (inBounds(newRow, newColumn)) {
                    set(newRow, newColumn, value);
                }

            }
        }
    }

}