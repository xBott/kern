import me.bottdev.kern.struct.algorithms.cycle.CycleDetector;
import me.bottdev.kern.struct.algorithms.toposort.TopologicalSort;
import me.bottdev.kern.struct.graph.EndpointPair;
import me.bottdev.kern.struct.graph.Graph;
import me.bottdev.kern.struct.graph.adjacency.AdjacencyListGraphBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;

import java.util.List;
import java.util.stream.Stream;

class TopologicalSortTests {

    private static TopologicalSort<String, String> topologicalSort;

    @BeforeAll
    static void setUp() {
        CycleDetector<String, String> cycleDetector = CycleDetector.identityNormalizer();
        topologicalSort = new TopologicalSort<>(cycleDetector);
    }

    @ParameterizedTest
    @MethodSource("graphs")
    void testGraphTopologicalSort(Graph<String, EndpointPair<String>> graph, String graphId, boolean expectDag) {

        System.out.printf("Applying topological sort to %s:%n", graphId);

        var result = topologicalSort.sort(graph);

        if (expectDag) {

            Assertions.assertTrue(result.isOk(), "Graph should be DAG");

            List<String> sortedNodes = result.unwrap();

            Assertions.assertEquals(graph.nodeCount(), sortedNodes.size(),
                    "All nodes must be present in topological order");

            for (EndpointPair<String> edge : graph.edges()) {

                int from = sortedNodes.indexOf(edge.nodeU());
                int to = sortedNodes.indexOf(edge.nodeV());

                Assertions.assertTrue(from < to,
                        "Invalid topological order for edge " + edge);
            }

            System.out.println("Order of sorted graph is:");
            System.out.println(sortedNodes);

        } else {

            Assertions.assertTrue(result.isError(),
                    "Graph does not contain a cycle, topological sort is not failed");

            System.out.println(result.unwrapError());

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
                        .addEdge(EndpointPair.directed("Node1", "Node3"))
                        .addEdge(EndpointPair.directed("Node1", "Node2"))
                        .addEdge(EndpointPair.directed("Node3", "Node4"))
                        .addEdge(EndpointPair.directed("Node2", "Node4"))
                        .addEdge(EndpointPair.directed("Node4", "Node5"))
                        .immutable();

        Graph<String, EndpointPair<String>> g2 =
                new AdjacencyListGraphBuilder<String, EndpointPair<String>>()
                        .addNode("A")
                        .addNode("B")
                        .addNode("C")
                        .addNode("D")
                        .addEdge(EndpointPair.directed("A", "C"))
                        .addEdge(EndpointPair.directed("B", "C"))
                        .addEdge(EndpointPair.directed("C", "D"))
                        .immutable();

        Graph<String, EndpointPair<String>> g3 =
                new AdjacencyListGraphBuilder<String, EndpointPair<String>>()
                        .addNode("X")
                        .addNode("Y")
                        .addNode("Z")
                        .addEdge(EndpointPair.directed("X", "Y"))
                        .addEdge(EndpointPair.directed("Y", "Z"))
                        .addEdge(EndpointPair.directed("Z", "X")) // cycle
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
                        .addEdge(EndpointPair.directed("N1", "N2"))
                        .addEdge(EndpointPair.directed("N1", "N3"))
                        .addEdge(EndpointPair.directed("N2", "N4"))
                        .addEdge(EndpointPair.directed("N2", "N5"))
                        .addEdge(EndpointPair.directed("N3", "N6"))
                        .addEdge(EndpointPair.directed("N4", "N7"))
                        .addEdge(EndpointPair.directed("N5", "N7"))
                        .addEdge(EndpointPair.directed("N6", "N8"))
                        .addEdge(EndpointPair.directed("N7", "N9"))
                        .addEdge(EndpointPair.directed("N8", "N9"))
                        .addEdge(EndpointPair.directed("N9", "N10"))
                        .immutable();

        return Stream.of(
                Arguments.of(g1, "g1", true),
                Arguments.of(g2, "g2", true),
                Arguments.of(g3, "g3", false),
                Arguments.of(g4, "g4", true)
        );
    }
}
