package me.bottdev.kern.struct.algorithms.cycle;

import me.bottdev.kern.struct.graph.EndpointPair;
import me.bottdev.kern.struct.graph.Graph;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CycleDetector<N, K extends Comparable<K>> {

    public static <N extends Comparable<N>> CycleDetector<N, N> identityNormalizer() {
        return new CycleDetector<N, N>(Function.identity());
    }

    private final Function<N, K> normalizer;

    public CycleDetector(Function<N, K> normalizer) {
        this.normalizer = normalizer;
    }

    public <E extends EndpointPair<N>> Optional<CyclePath<N>> detectFirst(Graph<N, E> graph) {
        Set<List<N>> foundCycles = new HashSet<>();
        Set<N> globalVisited = new HashSet<>();

        for (N start : graph.nodes()) {
            //Skip cycles, handled in previousDFS
            if (globalVisited.contains(start)) continue;

            if (dfs(graph, start, new ArrayList<>(), new LinkedHashSet<>(), foundCycles, globalVisited, true)) {
                List<N> cycle = foundCycles.iterator().next();
                return Optional.of(new CyclePath<>(cycle.getFirst(), cycle));
            }
        }

        return Optional.empty();
    }

    public <E extends EndpointPair<N>> CycleResult<N> detectAll(Graph<N, E> graph) {
        Set<List<N>> allCycles = new HashSet<>();
        Set<N> globalVisited = new HashSet<>();

        for (N start : graph.nodes()) {
            if (globalVisited.contains(start)) continue;
            dfs(graph, start, new ArrayList<>(), new LinkedHashSet<>(), allCycles, globalVisited, false);
        }

        Set<CyclePath<N>> cycles = allCycles.stream()
                .map(list -> new CyclePath<>(list.getFirst(), list))
                .collect(Collectors.toSet());

        return new CycleResult<>(cycles);
    }

    private <E extends EndpointPair<N>> boolean dfs(
            Graph<N, E> graph,
            N node,
            List<N> path,
            LinkedHashSet<N> pathSet,
            Set<List<N>> allCycles,
            Set<N> globalVisited,
            boolean stopOnFirst
    ) {
        // Found cycle (node is already in the path)
        if (pathSet.contains(node)) {
            List<N> cycleNodes = new ArrayList<>(pathSet);
            int index = cycleNodes.indexOf(node);
            // Take a part of the path starting from the first node entry
            List<N> cycle = new ArrayList<>(cycleNodes.subList(index, cycleNodes.size()));
            normalizeCycle(cycle);
            allCycles.add(cycle);
            return true;
        }

        // Node is completely handled in anther DFS, won't pass any cycles though it
        if (globalVisited.contains(node)) return false;

        path.add(node);
        pathSet.add(node);

        boolean found = false;
        for (E edge : graph.outEdges(node)) {
            Optional<N> neighborOpt = edge.reachableFrom(node);
            if (neighborOpt.isEmpty()) continue;

            N neighbor = neighborOpt.get();
            if (dfs(graph, neighbor, path, pathSet, allCycles, globalVisited, stopOnFirst)) {
                found = true;
                if (stopOnFirst) break; //Interrupt on first found cycle
            }
        }

        path.removeLast();
        pathSet.remove(node);
        globalVisited.add(node); // Mark node as completely handled

        return found;
    }

    private void normalizeCycle(List<N> cycle) {
        if (cycle.isEmpty()) return;

        int minIndex = 0;
        K minValue = normalizer.apply(cycle.getFirst());

        for (int i = 1; i < cycle.size(); i++) {
            K value = normalizer.apply(cycle.get(i));
            if (value.compareTo(minValue) < 0) {
                minIndex = i;
                minValue = value;
            }
        }

        Collections.rotate(cycle, -minIndex);
    }
}
