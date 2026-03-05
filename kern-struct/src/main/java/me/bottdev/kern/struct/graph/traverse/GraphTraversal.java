package me.bottdev.kern.struct.graph.traverse;

import me.bottdev.kern.struct.graph.EndpointPair;
import me.bottdev.kern.struct.graph.Graph;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class GraphTraversal<N, E extends EndpointPair<N>> {

    private TraversalOrder<N, E> order = TraversalOrders.dfs();

    public GraphTraversal<N, E> order(TraversalOrder<N, E> order) {
        this.order = order;
        return this;
    }

    public Stream<TraversalStep<N, E>> stream(Graph<N, E> graph, N start) {

        Iterator<TraversalStep<N, E>> iterator = order.createIterator(graph, start);

        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED),
                false
        );

    }

}
