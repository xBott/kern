package me.bottdev.kern.struct.graph.traverse.orders;

import me.bottdev.kern.struct.graph.EndpointPair;
import me.bottdev.kern.struct.graph.Graph;
import me.bottdev.kern.struct.graph.traverse.TraversalOrder;
import me.bottdev.kern.struct.graph.traverse.TraversalStep;

import java.util.*;

public class BfsTraversalOrder<N, E extends EndpointPair<N>> implements TraversalOrder<N, E> {

    @Override
    public Iterator<TraversalStep<N, E>> createIterator(Graph<N, E> graph, N start) {
        return new DfsIterator<>(graph, start);
    }

    private static class DfsIterator<N, E extends EndpointPair<N>> implements Iterator<TraversalStep<N, E>> {

        private final Graph<N, E> graph;
        private final Queue<BfsStep> queue = new ArrayDeque<>();
        private final Set<N> visited = new HashSet<>();
        private boolean stopped = false;

        DfsIterator(Graph<N, E> graph, N start) {
            this.graph = graph;
            enqueue(start, null, null, 0);
        }

        @Override
        public boolean hasNext() {
            return !stopped && !queue.isEmpty();
        }

        @Override
        public BfsStep next() {
            if (!hasNext()) throw new NoSuchElementException();
            BfsStep step = queue.poll();

            graph.outEdges(step.node()).forEach(edge ->
                    edge.reachableFrom(step.node()).ifPresent(neighbor -> {
                        if (!visited.contains(neighbor)) {
                            enqueue(neighbor, step.node(), edge, step.depth() + 1);
                        }
                    })
            );

            return step;
        }

        private void enqueue(N node, N parent, E parentEdge, int depth) {
            if (visited.add(node)) {
                queue.add(new BfsStep(node, parent, parentEdge, depth));
            }
        }

        private class BfsStep implements TraversalStep<N, E> {

            private final N node;
            private final N parent;
            private final E parentEdge;
            private final int depth;

            BfsStep(N node, N parent, E parentEdge, int depth) {
                this.node       = node;
                this.parent     = parent;
                this.parentEdge = parentEdge;
                this.depth      = depth;
            }

            @Override
            public N node() { return node; }
            @Override
            public int depth() { return depth; }
            @Override
            public Optional<N> parent() { return Optional.ofNullable(parent); }
            @Override
            public Optional<E> parentEdge() { return Optional.ofNullable(parentEdge); }
            @Override
            public Set<N> visited() { return Collections.unmodifiableSet(visited); }
            @Override
            public Graph<N, E> graph() { return graph; }

            @Override
            public void stop() {
                BfsTraversalOrder.DfsIterator.this.stopped = true;
                queue.clear();
            }

        }

    }

}
