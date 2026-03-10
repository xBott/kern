package me.bottdev.kern.struct;

public interface NeighborProvider<N> {

    Iterable<N> neighbors(N node);

}
