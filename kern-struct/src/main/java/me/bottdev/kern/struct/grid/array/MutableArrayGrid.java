package me.bottdev.kern.struct.grid.array;

import me.bottdev.kern.struct.grid.Grid;
import me.bottdev.kern.struct.grid.GridFitResult;
import me.bottdev.kern.struct.grid.MutableGrid;

public class MutableArrayGrid<T> extends ArrayGrid<T> implements MutableGrid<T> {

    public MutableArrayGrid(int width, int height, double angle) {
        super(width, height, angle);
    }

    public MutableArrayGrid(T[][] source, double angle) {
        super(source, angle);
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


}