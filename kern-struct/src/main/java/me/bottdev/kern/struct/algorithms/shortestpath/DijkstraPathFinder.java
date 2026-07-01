package me.bottdev.kern.struct.algorithms.shortestpath;

import me.bottdev.kern.struct.PathStructure;
import me.bottdev.kern.struct.paths.WeightedPath;

import java.util.LinkedList;

public class DijkstraPathFinder implements PathFinder {

    @Override
    public <N, T extends PathStructure<N>> WeightedPath<N> find(T structure, N start, N target) {

        DijkstraState<N> state = initialize(start);

        run(structure, state, target);

        return buildResult(state, start, target);
    }

    private <N> DijkstraState<N> initialize(N start) {

        DijkstraState<N> state = DijkstraState.empty();
        state.updateDistance(start, 0.0);
        state.enqueue(start);

        return state;
    }

    private <N, T extends PathStructure<N>> void run(T structure, DijkstraState<N> state, N target) {

        while (state.hasNext()) {

            N current = state.next();

            if (state.isVisited(current)) {
                continue;
            }

            state.markVisited(current);

            if (current.equals(target)) {
                return;
            }

            for (N neighbor : structure.neighbors(current)) {

                double weight = structure.weightBetween(current, neighbor);
                double updatedDistance = state.distance(current) + weight;

                if (updatedDistance < state.distance(neighbor)) {
                    state.updateDistance(neighbor, updatedDistance);
                    state.updatePredecessor(neighbor, current);
                    state.enqueue(neighbor);
                }

            }

        }

    }

    private <N> WeightedPath<N> buildResult(DijkstraState<N> state, N start, N target) {

        double distance = state.distance(target);
        LinkedList<N> path = new LinkedList<>();

        for (N at = target; at != null; at = state.predecessor(at)) {
            path.addFirst(at);
        }

        return new WeightedPath<>(start, target, distance, path);
    }

}

