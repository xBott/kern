package me.bottdev.kern.struct.algorithms.traverse.orders;

import me.bottdev.kern.struct.graph.EndpointPair;
import me.bottdev.kern.struct.graph.Graph;
import me.bottdev.kern.struct.algorithms.traverse.TraversalOrder;
import me.bottdev.kern.struct.algorithms.traverse.TraversalStep;

import java.util.*;

public class DfsTraversalOrder<N, E extends EndpointPair<N>> implements TraversalOrder<N, E> {

    @Override
    public Iterator<TraversalStep<N, E>> createIterator(Graph<N, E> graph, N start, boolean allowDuplicates) {
        return new DfsIterator<>(graph, start, allowDuplicates);
    }

    private static class DfsIterator<N, E extends EndpointPair<N>> implements Iterator<TraversalStep<N, E>> {

        private final Graph<N, E> graph;
        private final boolean allowDuplicates;
        private final Deque<DfsStep> stack = new ArrayDeque<>();
        private final Set<N> discovered = new HashSet<>();
        private final Set<N> visited = new HashSet<>();


        private boolean skipChildren = false;
        private boolean stopped = false;

        DfsIterator(Graph<N, E> graph, N start, boolean allowDuplicates) {
            this.graph = graph;
            this.allowDuplicates = allowDuplicates;
            stack.push(new DfsStep(start, null, null, 0));
            discovered.add(start);
        }

        @Override
        public boolean hasNext() {
            if (stopped) return false;
            return !stack.isEmpty();
        }

        @Override
        public TraversalStep<N, E> next() {
            if (!hasNext()) throw new NoSuchElementException();

            DfsStep current = stack.pop();
            visited.add(current.node);

            if (!skipChildren) {
                pushChildren(current);
            }
            skipChildren = false;

            return current;

        }

        private void pushChildren(DfsStep parent) {

            List<E> edges = new ArrayList<>(graph.outEdges(parent.node()));

            for (int i = edges.size() - 1; i >= 0; i--) {

                E edge = edges.get(i);

                edge.reachableFrom(parent.node()).ifPresent(neighbor -> {

                    if (allowDuplicates || discovered.add(neighbor)) {
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

        private class DfsStep implements TraversalStep<N, E> {

            private final N node;
            private final N parent;
            private final E parentEdge;
            private final int depth;

            private Set<N> visitedView;

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
            public Set<N> visited() {
                if (visitedView == null) {
                    visitedView = Collections.unmodifiableSet(visited);
                }
                return visitedView;
            }

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
