package me.bottdev.kern.struct;

public interface WeightProvider<N> {

    double weightBetween(N from, N to);

}
