package me.bottdev.kern.struct.algorithms.traverse.orders;

import me.bottdev.kern.struct.NeighborProvider;
import me.bottdev.kern.struct.algorithms.traverse.TraversalIterator;
import me.bottdev.kern.struct.algorithms.traverse.TraversalOrder;
import me.bottdev.kern.struct.algorithms.traverse.TraversalStep;

import java.util.*;

public class DfsTraversalOrder implements TraversalOrder {

    @Override
    public <N, T extends NeighborProvider<N>> TraversalIterator<N, T> createIterator(
            T structure,
            N start,
            boolean allowDuplicates
    ) {
        return new DfsIterator<>(structure, start, allowDuplicates);
    }

    private static class DfsIterator<N, T extends NeighborProvider<N>> implements TraversalIterator<N, T> {

        private final T structure;
        private final boolean allowDuplicates;

        private final Deque<DfsStep> stack = new ArrayDeque<>();
        private final Set<N> discovered = new HashSet<>();
        private final Set<N> visited = new HashSet<>();

        private boolean skipChildren = false;
        private boolean stopped = false;

        DfsIterator(T structure, N start, boolean allowDuplicates) {
            this.structure = structure;
            this.allowDuplicates = allowDuplicates;

            stack.push(new DfsStep(start, null, 0));
            discovered.add(start);
        }

        @Override
        public boolean hasNext() {
            if (stopped) return false;
            return !stack.isEmpty();
        }

        @Override
        public DfsStep next() {
            if (!hasNext()) throw new NoSuchElementException();

            DfsStep current = stack.pop();
            visited.add(current.node);

            if (!skipChildren) {
                pushChildren(current);
            }
            skipChildren = false;

            return current;

        }

        private void pushChildren(DfsStep step) {

            N parent = step.node();

            for (N neighbor : structure.neighbors(parent)) {

                if (allowDuplicates || discovered.add(neighbor)) {

                    DfsStep nextStep = new DfsStep(
                            neighbor,
                            parent,
                            step.depth() + 1
                    );
                    stack.push(nextStep);

                }

            }

        }

        public class DfsStep implements TraversalStep<N, T> {

            private final N node;
            private final N parent;
            private final int depth;

            private Set<N> visitedView;

            DfsStep(
                    N node,
                    N parent,
                    int depth
            ) {
                this.node = node;
                this.parent     = parent;
                this.depth      = depth;
            }

            @Override
            public N node() { return node; }

            @Override
            public int depth() { return depth; }

            @Override
            public Optional<N> parent() { return Optional.ofNullable(parent); }

            @Override
            public Set<N> visited() {
                if (visitedView == null) {
                    visitedView = Collections.unmodifiableSet(visited);
                }
                return visitedView;
            }

            @Override
            public T structure() { return structure; }

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
