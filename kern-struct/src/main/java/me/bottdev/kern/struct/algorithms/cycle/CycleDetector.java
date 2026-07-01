package me.bottdev.kern.struct.algorithms.cycle;

import me.bottdev.kern.struct.paths.CyclePath;
import me.bottdev.kern.struct.graph.EndpointPair;
import me.bottdev.kern.struct.graph.Graph;

import java.util.*;

public interface CycleDetector {

    <N, E extends EndpointPair<N>> Optional<CyclePath<N>> detect(Graph<N, E> graph);

}
