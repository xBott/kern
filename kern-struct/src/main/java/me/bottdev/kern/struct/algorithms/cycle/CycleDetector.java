package me.bottdev.kern.struct.algorithms.cycle;

import me.bottdev.kern.struct.graph.EndpointPair;
import me.bottdev.kern.struct.graph.Graph;

import java.util.*;

public interface CycleDetector<N> {

    <E extends EndpointPair<N>> Optional<CyclePath<N>> detectFirst(Graph<N, E> graph);

    <E extends EndpointPair<N>> CycleResult<N> detectAll(Graph<N, E> graph);

}
