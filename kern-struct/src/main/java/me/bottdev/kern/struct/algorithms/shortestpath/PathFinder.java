package me.bottdev.kern.struct.algorithms.shortestpath;

import me.bottdev.kern.struct.PathStructure;
import me.bottdev.kern.struct.paths.WeightedPath;

public interface PathFinder {

    <N, T extends PathStructure<N>> WeightedPath<N> find(T structure, N start, N target);

}
