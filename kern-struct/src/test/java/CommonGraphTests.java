import me.bottdev.kern.struct.algorithms.cycle.CycleDetector;
import me.bottdev.kern.struct.algorithms.cycle.CyclePath;
import me.bottdev.kern.struct.algorithms.cycle.CycleResult;
import me.bottdev.kern.struct.algorithms.shortestpath.Dijkstra;
import me.bottdev.kern.struct.algorithms.shortestpath.DijkstraPath;
import me.bottdev.kern.struct.graph.EndpointPair;
import me.bottdev.kern.struct.graph.Graph;
import me.bottdev.kern.struct.graph.GraphPathAdapter;
import me.bottdev.kern.struct.graph.MutableGraph;
import me.bottdev.kern.struct.graph.adjacency.AdjacencyListGraphBuilder;
import me.bottdev.kern.struct.algorithms.traverse.GraphTraversal;
import me.bottdev.kern.struct.algorithms.traverse.TraversalOrders;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public class CommonGraphTests {

    private static GraphTraversal<String, EndpointPair<String>> dfs;
    private static GraphTraversal<String, EndpointPair<String>> bfs;
    private static Dijkstra dijkstra;
    private static CycleDetector<String, String> cycleDetector;

    @BeforeAll
    public static void setup() {
        dfs = new GraphTraversal<String, EndpointPair<String>>()
                .order(TraversalOrders.dfs());

        bfs = new GraphTraversal<String, EndpointPair<String>>()
                .order(TraversalOrders.bfs());

        dijkstra = new Dijkstra();
        cycleDetector = CycleDetector.identityNormalizer();
    }

    @Test
    public void testGraphCreation_AdjacencyList_Immutable() {

        System.out.println("Test immutable adjacency list graph: ");

        Graph<String, EndpointPair<String>> graph = new AdjacencyListGraphBuilder<String, EndpointPair<String>>()
                .addNode("Node1")
                .addNode("Node2")
                .addNode("Node3")
                .addEdge(EndpointPair.directed("Node1", "Node3"))
                .addEdge(EndpointPair.directed("Node3", "Node2"))
                .immutable();

        Assertions.assertNotNull(graph, "Graph should not be null");
        Assertions.assertEquals(3, graph.nodeCount(), "Graph should have 3 nodes");
        Assertions.assertEquals(2, graph.edgeCount(), "Graph should have 2 edges");

        dfs.stream(graph, "Node1")
                .forEach(step -> {
                    System.out.println(step.node());
                });

    }

    @Test
    public void testGraphCreation_AdjacencyList_Mutable() {

        System.out.println("Test mutable adjacency list graph: ");

        MutableGraph<String, EndpointPair<String>> graph = new AdjacencyListGraphBuilder<String, EndpointPair<String>>()
                .addNode("Node1")
                .addNode("Node2")
                .addNode("Node3")
                .addEdge(EndpointPair.directed("Node1", "Node3"))
                .addEdge(EndpointPair.directed("Node3", "Node2"))
                .mutable();

        graph.addNode("Node4");
        graph.addNode("Node5");
        graph.addEdge(EndpointPair.directed("Node1", "Node4"));
        graph.addEdge(EndpointPair.directed("Node4", "Node2"));
        graph.addEdge(EndpointPair.directed("Node4", "Node5"));

        Assertions.assertNotNull(graph, "Graph should not be null");
        Assertions.assertEquals(5, graph.nodeCount(), "Graph should have 5 nodes");
        Assertions.assertEquals(5, graph.edgeCount(), "Graph should have 5 edges");

        bfs.stream(graph, "Node1")
                .forEach(step -> {
                    System.out.println(step.node());
                });

    }

    private Graph<String, EndpointPair.WeightedUndirected<String>> buildWeightedGraph() {

        return new AdjacencyListGraphBuilder<String, EndpointPair.WeightedUndirected<String>>()
                .addNode("Node1")
                .addNode("Node2")
                .addNode("Node3")
                .addNode("Node4")
                .addNode("Node5")
                .addNode("Node6")
                .addNode("Node7")
                .addNode("Node8")
                .addNode("Node9")
                .addNode("Node10")
                .addEdge(EndpointPair.undirected("Node1", "Node2", 10))
                .addEdge(EndpointPair.undirected("Node1", "Node3", 8))
                .addEdge(EndpointPair.undirected("Node2", "Node3", 3))
                .addEdge(EndpointPair.undirected("Node2", "Node5", 4))
                .addEdge(EndpointPair.undirected("Node3", "Node5", 2))
                .addEdge(EndpointPair.undirected("Node3", "Node4", 3))
                .addEdge(EndpointPair.undirected("Node3", "Node6", 1))
                .addEdge(EndpointPair.undirected("Node4", "Node7", 10))
                .addEdge(EndpointPair.undirected("Node5", "Node8", 2))
                .addEdge(EndpointPair.undirected("Node6", "Node7", 3))
                .addEdge(EndpointPair.undirected("Node6", "Node8", 5))
                .addEdge(EndpointPair.undirected("Node6", "Node10", 1))
                .addEdge(EndpointPair.undirected("Node8", "Node9", 2))
                .addEdge(EndpointPair.undirected("Node9", "Node10", 3))
                .immutable();
    }

    static Stream<Arguments> dijkstraCases() {
        return Stream.of(
                Arguments.of(
                        "Node3",
                        "Node3",
                        List.of("Node3")
                ),
                Arguments.of(
                        "Node1",
                        "Node9",
                        List.of("Node1", "Node3", "Node6", "Node10", "Node9")
                ),
                Arguments.of(
                        "Node1",
                        "Node7",
                        List.of("Node1", "Node3", "Node6", "Node7")
                ),
                Arguments.of(
                        "Node2",
                        "Node8",
                        List.of("Node2", "Node5", "Node8")
                ),
                Arguments.of(
                        "Node3",
                        "Node10",
                        List.of("Node3", "Node6", "Node10")
                ),
                Arguments.of(
                        "Node2",
                        "Node10",
                        List.of("Node2", "Node3", "Node6", "Node10")
                )
        );
    }

    @ParameterizedTest
    @MethodSource("dijkstraCases")
    void testGraphDijkstra(String start, String end, List<String> expectedPath) {

        System.out.printf("Test dijkstra case from %s to %s: %n", start, end);

        Graph<String, EndpointPair.WeightedUndirected<String>> graph = buildWeightedGraph();

        GraphPathAdapter<String> adapter = new GraphPathAdapter<>(graph);

        DijkstraPath<String> path = dijkstra.shortestPath(adapter, start, end);

        Assertions.assertNotNull(path, "Shortest path should not be null");
        Assertions.assertEquals(expectedPath, path.nodes(), "Shortest path should be equal to expected");

        System.out.println("Shortest nodes from " + start + " to " + end + ":");
        path.nodes().forEach(node -> System.out.printf("|- %s%n", node));

    }

    @Test
    public void testGraphCycleDetection() {

        System.out.println("Test graph cycle detection: ");

        Graph<String, EndpointPair<String>> graph = new AdjacencyListGraphBuilder<String, EndpointPair<String>>()
                .addNode("Node1")
                .addNode("Node2")
                .addNode("Node3")
                .addNode("Node4")
                .addNode("Node5")
                .addEdge(EndpointPair.directed("Node1", "Node2"))
                .addEdge(EndpointPair.directed("Node2", "Node3"))
                .addEdge(EndpointPair.directed("Node3", "Node4"))
                .addEdge(EndpointPair.directed("Node4", "Node5"))
                .addEdge(EndpointPair.directed("Node5", "Node1"))
                .immutable();

        Assertions.assertNotNull(graph, "Graph should not be null");
        Assertions.assertEquals(5, graph.nodeCount(), "Graph should have 5 nodes");
        Assertions.assertEquals(5, graph.edgeCount(), "Graph should have 5 edges");

        //Test detect first
        Optional<CyclePath<String>> optional = cycleDetector.detectFirst(graph);
        Assertions.assertTrue(optional.isPresent(), "detectFirst should be present");

        System.out.println(optional.get());

        //Test detect all
        CycleResult<String> result = cycleDetector.detectAll(graph);
        Assertions.assertEquals(1, result.amount(), "detectAll should detect 1 unique cycle");

        CyclePath<String> first = result.cycles().iterator().next();
        Assertions.assertEquals(first, optional.get(), "Cycle from detectFirst should equal first cycle from detectAll");

        System.out.println(result);

    }

}
