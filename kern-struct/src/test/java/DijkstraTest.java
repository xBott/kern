import me.bottdev.kern.struct.algorithms.shortestpath.Dijkstra;
import me.bottdev.kern.struct.algorithms.shortestpath.DijkstraPath;
import me.bottdev.kern.struct.graph.EndpointPair;
import me.bottdev.kern.struct.graph.Graph;
import me.bottdev.kern.struct.graph.GraphPathAdapter;
import me.bottdev.kern.struct.graph.adjacency.AdjacencyListGraphBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

public class DijkstraTest {

    private static Dijkstra dijkstra;

    @BeforeAll
    static void setUp() {
        dijkstra = new Dijkstra();
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

}
