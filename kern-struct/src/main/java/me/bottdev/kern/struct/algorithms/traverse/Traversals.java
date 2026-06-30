package me.bottdev.kern.struct.algorithms.traverse;

import me.bottdev.kern.struct.algorithms.traverse.orders.BfsTraversalOrder;
import me.bottdev.kern.struct.algorithms.traverse.orders.DfsTraversalOrder;

public final class Traversals {

    private static final TraversalOrder dfsPreOrder = new DfsTraversalOrder();
    private static final TraversalOrder bfsOrder = new BfsTraversalOrder();

    public static TraversalStructureSelector dfsPreOrder() {
        return new TraversalStructureSelector(dfsPreOrder);
    }

    public static TraversalStructureSelector bfs() {
        return new TraversalStructureSelector(bfsOrder);
    }

}
