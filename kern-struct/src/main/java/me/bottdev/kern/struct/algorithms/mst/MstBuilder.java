package me.bottdev.kern.struct.algorithms.mst;

import me.bottdev.kern.struct.graph.EndpointPair;
import me.bottdev.kern.struct.graph.Graph;
import me.bottdev.kern.struct.graph.GraphBuilder;
import me.bottdev.kern.struct.graph.endpoints.Weighted;

import java.util.Optional;

public interface MstBuilder {

    <N, E extends EndpointPair<N> & Weighted> Optional<Graph<N, E>> apply(
            Graph<N, E> graph,
            GraphBuilder<N, E> mstBuilder
    );

}
