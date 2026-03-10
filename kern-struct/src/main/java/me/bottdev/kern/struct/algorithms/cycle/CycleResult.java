package me.bottdev.kern.struct.algorithms.cycle;

import java.util.Set;
import java.util.stream.Collectors;

public record CycleResult<N>(
        Set<CyclePath<N>> cycles
) {

    public int amount() {
        return cycles.size();
    }

    public boolean hasCycles() {
        return !cycles.isEmpty();
    }

    @Override
    public String toString() {
        if (!hasCycles()) {
            return "Cycle Result: empty";
        }

        return "Cycle Result: \n" + cycles.stream().map(path -> " - " + path).collect(Collectors.joining("\n"));
    }
}
