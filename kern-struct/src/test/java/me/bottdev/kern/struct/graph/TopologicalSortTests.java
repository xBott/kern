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
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

class TopologicalSortTests {

    private static TopologicalSorter topologicalSort;

    @BeforeAll
    static void setUp() {
        CycleDetector cycleDetector = new SimpleCycleDetector();
        topologicalSort = new KahnSorter(cycleDetector);
    }

    @ParameterizedTest(name = "{1}")
    @MethodSource("acyclicGraphs")
    void sortsAcyclicGraph(
            Graph<String, Directed<String>> graph,
            String graphId
    ) throws CircularDependencyException {

        System.out.printf(
                "Applying topological sort to %s:%n",
                graphId
        );

        var result = topologicalSort.sort(graph);
        List<String> ordered = result.ordered();

        Assertions.assertEquals(
                graph.nodeCount(),
                ordered.size(),
                "All nodes must be present in topological order"
        );

        Assertions.assertEquals(
                graph.nodeCount(),
                ordered.stream().distinct().count(),
                "Every node must appear exactly once"
        );

        for (EndpointPair<String> edge : graph.edges()) {

            int sourceIndex = ordered.indexOf(edge.nodeU());
            int targetIndex = ordered.indexOf(edge.nodeV());

            Assertions.assertTrue(
                    sourceIndex > targetIndex,
                    "Invalid dependency order for edge " + edge
                            + ". Expected source after target, but got: "
                            + ordered
            );
        }

        System.out.println("Order of sorted graph:");
        System.out.println(ordered);
        System.out.println();
    }

    @ParameterizedTest(name = "{1}")
    @MethodSource("cyclicGraphs")
    void rejectsCyclicGraph(
            Graph<String, Directed<String>> graph,
            String graphId
    ) {

        System.out.printf(
                "Applying topological sort to cyclic graph %s:%n",
                graphId
        );

        CircularDependencyException exception =
                Assertions.assertThrows(
                        CircularDependencyException.class,
                        () -> topologicalSort.sort(graph)
                );

        Assertions.assertNotNull(
                exception,
                "CircularDependencyException must be thrown"
        );

        System.out.println(
                "Detected cycle: " + exception.getMessage()
        );
    }

    static Stream<Arguments> acyclicGraphs() {

        Graph<String, Directed<String>> g1 =
                new AdjacencyListGraphBuilder<String, Directed<String>>()
                        .addNode("Node1")
                        .addNode("Node2")
                        .addNode("Node3")
                        .addNode("Node4")
                        .addNode("Node5")
                        .addEdge(
                                EndpointPairs.directed("Node1", "Node3")
                        )
                        .addEdge(
                                EndpointPairs.directed("Node1", "Node2")
                        )
                        .addEdge(
                                EndpointPairs.directed("Node3", "Node4")
                        )
                        .addEdge(
                                EndpointPairs.directed("Node2", "Node4")
                        )
                        .addEdge(
                                EndpointPairs.directed("Node4", "Node5")
                        )
                        .immutable();


        Graph<String, Directed<String>> g2 =
                new AdjacencyListGraphBuilder<String, Directed<String>>()
                        .addNode("A")
                        .addNode("B")
                        .addNode("C")
                        .addNode("D")
                        .addEdge(
                                EndpointPairs.directed("A", "C")
                        )
                        .addEdge(
                                EndpointPairs.directed("B", "C")
                        )
                        .addEdge(
                                EndpointPairs.directed("C", "D")
                        )
                        .immutable();

        Graph<String, Directed<String>> g4 =
                new AdjacencyListGraphBuilder<String, Directed<String>>()
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
                        .addEdge(
                                EndpointPairs.directed("N1", "N2")
                        )
                        .addEdge(
                                EndpointPairs.directed("N1", "N3")
                        )
                        .addEdge(
                                EndpointPairs.directed("N2", "N4")
                        )
                        .addEdge(
                                EndpointPairs.directed("N2", "N5")
                        )
                        .addEdge(
                                EndpointPairs.directed("N3", "N6")
                        )
                        .addEdge(
                                EndpointPairs.directed("N4", "N7")
                        )
                        .addEdge(
                                EndpointPairs.directed("N5", "N7")
                        )
                        .addEdge(
                                EndpointPairs.directed("N6", "N8")
                        )
                        .addEdge(
                                EndpointPairs.directed("N7", "N9")
                        )
                        .addEdge(
                                EndpointPairs.directed("N8", "N9")
                        )
                        .addEdge(
                                EndpointPairs.directed("N9", "N10")
                        )
                        .immutable();

        return Stream.of(
                Arguments.of(g1, "g1"),
                Arguments.of(g2, "g2"),
                Arguments.of(g4, "g4")
        );
    }

    static Stream<Arguments> cyclicGraphs() {

        /*
         * A → B → C → A
         */
        Graph<String, Directed<String>> cycle1 =
                new AdjacencyListGraphBuilder<String, Directed<String>>()
                        .addNode("A")
                        .addNode("B")
                        .addNode("C")
                        .addEdge(
                                EndpointPairs.directed("A", "B")
                        )
                        .addEdge(
                                EndpointPairs.directed("B", "C")
                        )
                        .addEdge(
                                EndpointPairs.directed("C", "A")
                        )
                        .immutable();

        /*
         * A → B → C → D → B
         */
        Graph<String, Directed<String>> cycle2 =
                new AdjacencyListGraphBuilder<String, Directed<String>>()
                        .addNode("A")
                        .addNode("B")
                        .addNode("C")
                        .addNode("D")
                        .addEdge(
                                EndpointPairs.directed("A", "B")
                        )
                        .addEdge(
                                EndpointPairs.directed("B", "C")
                        )
                        .addEdge(
                                EndpointPairs.directed("C", "D")
                        )
                        .addEdge(
                                EndpointPairs.directed("D", "B")
                        )
                        .immutable();

        Graph<String, Directed<String>> cycle3 =
                new AdjacencyListGraphBuilder<String, Directed<String>>()
                        .addNode("A")
                        .addNode("B")
                        .addNode("C")
                        .addNode("D")
                        .addNode("E")
                        .addEdge(
                                EndpointPairs.directed("A", "B")
                        )
                        .addEdge(
                                EndpointPairs.directed("A", "C")
                        )
                        .addEdge(
                                EndpointPairs.directed("C", "B")
                        )
                        .addEdge(
                                EndpointPairs.directed("B", "D")
                        )
                        .addEdge(
                                EndpointPairs.directed("D", "E")
                        )
                        .addEdge(
                                EndpointPairs.directed("E", "B")
                        )
                        .immutable();

        Graph<String, Directed<String>> selfLoop =
                new AdjacencyListGraphBuilder<String, Directed<String>>()
                        .addNode("A")
                        .addEdge(
                                EndpointPairs.directed("A", "A")
                        )
                        .immutable();

        return Stream.of(
                Arguments.of(cycle1, "cycle1"),
                Arguments.of(cycle2, "cycle2"),
                Arguments.of(cycle3, "cycle3"),
                Arguments.of(selfLoop, "selfLoop")
        );

    }
}