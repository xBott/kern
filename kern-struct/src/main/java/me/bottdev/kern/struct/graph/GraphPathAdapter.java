package me.bottdev.kern.struct.graph;

import lombok.RequiredArgsConstructor;
import me.bottdev.kern.struct.PathStructure;
import me.bottdev.kern.struct.graph.endpoints.Weighted;
import me.bottdev.kern.struct.graph.exceptions.GraphStructureException;

import java.util.Set;

/// Adapts a weighted graph to the generic path structure API.
///
/// @param <N> node type
@RequiredArgsConstructor
public class GraphPathAdapter<N> implements PathStructure<N> {

    /// Graph used as the path structure source.
    public final Graph<N, ? extends Weighted> graph;

    @Override
    public Set<N> elements() {
        return graph.nodes();
    }

    @Override
    public Iterable<N> neighbors(N node) {
        return graph.successors(node);
    }

    @Override
    public double weightBetween(N from, N to) {
        return graph.edgeConnecting(from, to)
                .map(Weighted::weight)
                .orElseThrow(() -> new GraphStructureException("No edge between " + from + " and " + to));
    }

}
