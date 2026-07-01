package me.bottdev.kern.struct.graph;

public interface MutableGraphBuilder<N, E extends EndpointPair<N>> {

    MutableGraph<N, E> mutable();

}
