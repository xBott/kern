package me.bottdev.kern.struct.graph.traverse.orders;

import me.bottdev.kern.struct.graph.EndpointPair;
import me.bottdev.kern.struct.graph.Graph;
import me.bottdev.kern.struct.graph.traverse.TraversalOrder;
import me.bottdev.kern.struct.graph.traverse.TraversalStep;

import java.util.*;

public class DfsTraversalOrder<N, E extends EndpointPair<N>> implements TraversalOrder<N, E> {

    @Override
    public Iterator<TraversalStep<N, E>> createIterator(Graph<N, E> graph, N start) {
        return new DfsIterator<>(graph, start);
    }

    private static class DfsIterator<N, E extends EndpointPair<N>> implements Iterator<TraversalStep<N, E>> {

        private final Graph<N, E> graph;
        private final Deque<DfsStep> stack = new ArrayDeque<>();
        private final Set<N> visited = new HashSet<>();

        private boolean skipChildren = false;
        private boolean stopped = false;
        private DfsStep current = null;

        DfsIterator(Graph<N, E> graph, N start) {
            this.graph = graph;
            stack.push(new DfsStep(start, null, null, 0));
        }

        @Override
        public boolean hasNext() {
            if (stopped) return false;
            skipVisited();
            return !stack.isEmpty();
        }

        @Override
        public TraversalStep<N, E> next() {
            if (!hasNext()) throw new NoSuchElementException();

            current = stack.pop();
            visited.add(current.node());

            if (!skipChildren) {
                pushChildren(current);
            }
            skipChildren = false;

            return current;

        }

        private void pushChildren(DfsStep parent) {

            List<E> edges = new ArrayList<>(graph.outEdges(parent.node()));

            Collections.reverse(edges);
            for (E edge : edges) {

                edge.reachableFrom(parent.node()).ifPresent(neighbor -> {

                    if (!visited.contains(neighbor)) {
                        stack.push(new DfsStep(
                                neighbor,
                                parent.node(),
                                edge,
                                parent.depth() + 1
                        ));
                    }

                });

            }

        }

        private void skipVisited() {
            while (!stack.isEmpty() && visited.contains(stack.peek().node())) {
                stack.pop();
            }
        }

        private class DfsStep implements TraversalStep<N, E> {

            private final N node;
            private final N parent;
            private final E parentEdge;
            private final int depth;

            DfsStep(N node, N parent, E parentEdge, int depth) {
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

            public void skipSubtree() {
                DfsIterator.this.skipChildren = true;
            }

            @Override
            public void stop() {
                DfsIterator.this.stopped = true;
                stack.clear();
            }

        }

    }

}
