package me.bottdev.kern.struct.algorithms.traverse;

import me.bottdev.kern.struct.graph.EndpointPair;
import me.bottdev.kern.struct.algorithms.traverse.orders.BfsTraversalOrder;
import me.bottdev.kern.struct.algorithms.traverse.orders.DfsTraversalOrder;

public class TraversalOrders {

    public static <N, E extends EndpointPair<N>> TraversalOrder<N, E> dfs() {
        return new DfsTraversalOrder<>();
    }

    public static <N, E extends EndpointPair<N>> TraversalOrder<N, E> bfs() {
        return new BfsTraversalOrder<>();
    }

}
