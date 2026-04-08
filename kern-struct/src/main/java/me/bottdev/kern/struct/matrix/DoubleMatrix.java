package me.bottdev.kern.struct.matrix;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class DoubleMatrix extends Matrix<Double> {

    public static final DoubleMatrix ROTATE_90 = new DoubleMatrix(2, 2)
            .setRow(0, new Double[]{0.0, -1.0})
            .setRow(1, new Double[]{1.0,  0.0});

    public static final DoubleMatrix ROTATE_180 = new DoubleMatrix(2, 2)
            .setRow(0, new Double[]{-1.0,  0.0})
            .setRow(1, new Double[]{ 0.0, -1.0});

    public static final DoubleMatrix ROTATE_270 = new DoubleMatrix(2, 2)
            .setRow(0, new Double[]{ 0.0, 1.0})
            .setRow(1, new Double[]{-1.0, 0.0});

    public static final DoubleMatrix ROTATE_360 = new DoubleMatrix(2, 2)
            .setRow(0, new Double[]{1.0, 0.0})
            .setRow(1, new Double[]{0.0, 1.0});

    public static DoubleMatrix getRotationMatrix(double degrees) {
        double normalized = ((degrees % 360) + 360) % 360;

        if (normalized == 0.0)   return ROTATE_360;
        if (normalized == 90.0)  return ROTATE_90;
        if (normalized == 180.0) return ROTATE_180;
        if (normalized == 270.0) return ROTATE_270;

        double rad = Math.toRadians(degrees);
        double sin = Math.sin(rad);
        double cos = Math.cos(rad);

        return new DoubleMatrix(2, 2)
                .setRow(0, new Double[]{cos, -sin})
                .setRow(1, new Double[]{sin,  cos});
    }

    public DoubleMatrix(int rows, int columns) {
        super(Double.class, rows, columns);
    }

    public DoubleMatrix setRow(int row, Double[] values) {
        data[row] = values;
        return this;
    }

    public DoubleMatrix add(DoubleMatrix other) {
        if (!hasSameSize(other)) {
            throw new IllegalArgumentException("Matrices do not have same sizes");
        }

        DoubleMatrix result = new DoubleMatrix(rows, columns);
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {

                double first = get(row, column);
                double second = other.get(row, column);
                double sum = first + second;

                result.set(row, column, sum);

            }
        }

        return result;
    }

    public DoubleMatrix subtract(DoubleMatrix other) {
        if (!hasSameSize(other)) {
            throw new IllegalArgumentException("Matrices do not have same sizes");
        }

        DoubleMatrix result = new DoubleMatrix(rows, columns);
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {

                double first = get(row, column);
                double second = other.get(row, column);
                double subtract = first - second;

                result.set(row, column, subtract);

            }
        }

        return result;
    }

    public DoubleMatrix multiply(DoubleMatrix other) {

        if (columns != other.rows) {
            throw new IllegalArgumentException("Matrices can't be multiplied. First matrix's columns do not equals to second matrix's rows.");
        }

        DoubleMatrix result = new DoubleMatrix(rows, other.columns);
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < other.columns; column++) {
                for (int index = 0; index < columns; index++) {

                    double value = get(row, index) * other.get(index, column);
                    double previousValue = result.getOrDefault(row, column, 0.0);
                    double newValue = previousValue + value;
                    result.set(row, column, newValue);
                }

            }
        }

        return result;
    }


}
