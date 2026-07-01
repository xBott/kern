package me.bottdev.kern.struct.graph;

import me.bottdev.kern.struct.algorithms.cycle.CycleDetector;
import me.bottdev.kern.struct.algorithms.cycle.SimpleCycleDetector;
import me.bottdev.kern.struct.algorithms.sort.CircularDependencyException;
import me.bottdev.kern.struct.algorithms.sort.KahnSorter;
import me.bottdev.kern.struct.algorithms.sort.TopologicalSorter;
import me.bottdev.kern.struct.graph.adjacency.AdjacencyListGraphBuilder;
import me.bottdev.kern.struct.graph.endpoints.Directed;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;

import java.util.List;
import java.util.stream.Stream;

class TopologicalSortTests {

    private static TopologicalSorter topologicalSort;

    @BeforeAll
    static void setUp() {
        CycleDetector cycleDetector = new SimpleCycleDetector();
        topologicalSort = new KahnSorter(cycleDetector);
    }

    @ParameterizedTest
    @MethodSource("graphs")
    void testGraphTopologicalSort(Graph<String, Directed<String>> graph, String graphId, boolean expectDag)
            throws CircularDependencyException
    {

        System.out.printf("Applying topological sort to %s:%n", graphId);

        var result = topologicalSort.sort(graph);

        if (expectDag) {

            List<String> ordered = result.ordered();

            Assertions.assertEquals(graph.nodeCount(), ordered.size(),
                    "All nodes must be present in topological order");

            for (EndpointPair<String> edge : graph.edges()) {

                int from = ordered.indexOf(edge.nodeU());
                int to = ordered.indexOf(edge.nodeV());

                Assertions.assertTrue(from < to,
                        "Invalid topological order for edge " + edge);
            }

            System.out.println("Order of sorted graph is:");
            System.out.println(result);

        }
    }

    static Stream<Arguments> graphs() {

        Graph<String, EndpointPair<String>> g1 =
                new AdjacencyListGraphBuilder<String, EndpointPair<String>>()
                        .addNode("Node1")
                        .addNode("Node2")
                        .addNode("Node3")
                        .addNode("Node4")
                        .addNode("Node5")
                        .addEdge(EndpointPairs.directed("Node1", "Node3"))
                        .addEdge(EndpointPairs.directed("Node1", "Node2"))
                        .addEdge(EndpointPairs.directed("Node3", "Node4"))
                        .addEdge(EndpointPairs.directed("Node2", "Node4"))
                        .addEdge(EndpointPairs.directed("Node4", "Node5"))
                        .immutable();

        Graph<String, EndpointPair<String>> g2 =
                new AdjacencyListGraphBuilder<String, EndpointPair<String>>()
                        .addNode("A")
                        .addNode("B")
                        .addNode("C")
                        .addNode("D")
                        .addEdge(EndpointPairs.directed("A", "C"))
                        .addEdge(EndpointPairs.directed("B", "C"))
                        .addEdge(EndpointPairs.directed("C", "D"))
                        .immutable();

        Graph<String, EndpointPair<String>> g4 =
                new AdjacencyListGraphBuilder<String, EndpointPair<String>>()
                        .addNode("N1")
                        .addNode("N2")
                        .addNode("N3")
                        .addNode("N4")
                        .addNode("N5")
                        .addNode("N6")
                        .addNode("N7")
                        .addNode("N8")
                        .addNode("N9")
                        .addNode("N10")
                        .addEdge(EndpointPairs.directed("N1", "N2"))
                        .addEdge(EndpointPairs.directed("N1", "N3"))
                        .addEdge(EndpointPairs.directed("N2", "N4"))
                        .addEdge(EndpointPairs.directed("N2", "N5"))
                        .addEdge(EndpointPairs.directed("N3", "N6"))
                        .addEdge(EndpointPairs.directed("N4", "N7"))
                        .addEdge(EndpointPairs.directed("N5", "N7"))
                        .addEdge(EndpointPairs.directed("N6", "N8"))
                        .addEdge(EndpointPairs.directed("N7", "N9"))
                        .addEdge(EndpointPairs.directed("N8", "N9"))
                        .addEdge(EndpointPairs.directed("N9", "N10"))
                        .immutable();

        return Stream.of(
                Arguments.of(g1, "g1", true),
                Arguments.of(g2, "g2", true),
                Arguments.of(g4, "g4", true)
        );
    }
}
