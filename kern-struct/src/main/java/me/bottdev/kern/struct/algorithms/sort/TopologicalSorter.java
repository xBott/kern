package me.bottdev.kern.struct.algorithms.sort;

import me.bottdev.kern.struct.graph.Graph;
import me.bottdev.kern.struct.graph.endpoints.Directed;

public interface TopologicalSorter {

    <N> TopologicalSortResult<N> sort(Graph<N, ? extends Directed<N>> graph) throws CircularDependencyException;

}
