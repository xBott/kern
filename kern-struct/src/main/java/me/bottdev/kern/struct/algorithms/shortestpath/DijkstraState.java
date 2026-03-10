package me.bottdev.kern.struct.algorithms.shortestpath;

import java.util.*;

public class DijkstraState<N> {

    private final Map<N, Double> distances;
    private final Map<N, N> predecessors;
    private final Set<N> visited;
    private final PriorityQueue<N> queue;

    public DijkstraState(Map<N, Double> distances, Map<N, N> predecessors, Set<N> visited) {
        this.distances = distances;
        this.predecessors = predecessors;
        this.visited = visited;
        this.queue =  new PriorityQueue<>(
                Comparator.comparingDouble(this::distance)
        );
    }

    public static <N> DijkstraState<N> empty() {

        HashMap<N, Double> distances = new HashMap<>();
        HashMap<N, N> predecessors = new HashMap<>();
        Set<N> visited = new HashSet<>();

        return new DijkstraState<>(distances, predecessors, visited);

    }

    public double distance(N node) {
        return distances.getOrDefault(node, Double.POSITIVE_INFINITY);
    }

    public void updateDistance(N node, double distance) {
        distances.put(node, distance);
    }

    public N predecessor(N node) {
        return predecessors.get(node);
    }

    public void updatePredecessor(N node, N predecessor) {
        predecessors.put(node, predecessor);
    }

    public boolean isVisited(N node) {
        return visited.contains(node);
    }

    public void markVisited(N node) {
        visited.add(node);
    }

    public void enqueue(N node) {
        queue.add(node);
    }

    public boolean hasNext() {
        return !queue.isEmpty();
    }

    public N next() {
        return queue.poll();
    }

}
