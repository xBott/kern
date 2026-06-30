package me.bottdev.kern.struct.algorithms.traverse.orders;

import me.bottdev.kern.struct.NeighborProvider;
import me.bottdev.kern.struct.algorithms.traverse.TraversalIterator;
import me.bottdev.kern.struct.algorithms.traverse.TraversalOrder;
import me.bottdev.kern.struct.algorithms.traverse.TraversalStep;

import java.util.*;

public class BfsTraversalOrder implements TraversalOrder {

    @Override
    public <N, T extends NeighborProvider<N>> TraversalIterator<N, T> createIterator(
            T structure,
            N start,
            boolean allowDuplicates
    ) {
        return new BfsIterator<>(structure, start, allowDuplicates);
    }

    private static class BfsIterator<N, T extends NeighborProvider<N>> implements TraversalIterator<N, T> {

        private final T structure;
        private final boolean allowDuplicates;

        private final Queue<BfsStep> queue = new ArrayDeque<>();
        private final Set<N> discovered = new HashSet<>();
        private final Set<N> visited = new HashSet<>();

        private boolean stopped = false;

        BfsIterator(T structure, N start, boolean allowDuplicates) {
            this.structure = structure;
            this.allowDuplicates = allowDuplicates;
            enqueue(start, null, 0);
        }

        @Override
        public boolean hasNext() {
            if (stopped) return false;
            return !queue.isEmpty();
        }

        @Override
        public BfsStep next() {
            if (!hasNext()) throw new NoSuchElementException();

            BfsStep current = queue.poll();
            visited.add(current.node);

            pushChildren(current);

            return current;
        }

        private void pushChildren(BfsStep step) {
            N parent = step.node();
            for (N neighbor : structure.neighbors(parent)) {
                if (allowDuplicates || discovered.add(neighbor)) {
                    enqueue(neighbor, parent, step.depth() + 1);
                }
            }
        }

        private void enqueue(N node, N parent, int depth) {
            discovered.add(node);
            queue.add(new BfsStep(node, parent, depth));
        }

        private class BfsStep implements TraversalStep<N, T> {

            private final N node;
            private final N parent;
            private final int depth;
            private Set<N> visitedView;

            BfsStep(N node, N parent, int depth) {
                this.node   = node;
                this.parent = parent;
                this.depth  = depth;
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

            @Override
            public void stop() {
                BfsIterator.this.stopped = true;
                queue.clear();
            }
        }
    }
}