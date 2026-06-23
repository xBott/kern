package me.bottdev.kern.struct.graph;

import me.bottdev.kern.struct.graph.endpoints.Directed;
import me.bottdev.kern.struct.graph.endpoints.Undirected;
import me.bottdev.kern.struct.graph.endpoints.WeightedDirected;
import me.bottdev.kern.struct.graph.endpoints.WeightedUndirected;

public final class EndpointPairs {

    public static <N> Directed<N> directed(N u, N v) {
        return new Directed<>(u, v);
    }
    public static <N> Undirected<N> undirected(N u, N v) {
        return new Undirected<>(u, v);
    }
    public static <N> WeightedDirected<N> weightedDirected(N u, N v, double weight) {
        return new WeightedDirected<>(u, v, weight);
    }
    public static <N> WeightedUndirected<N> weightedUndirected(N u, N v, double weight) {
        return new WeightedUndirected<>(u, v, weight);
    }

}