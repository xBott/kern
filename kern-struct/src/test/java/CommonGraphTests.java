import me.bottdev.kern.struct.graph.EndpointPair;
import me.bottdev.kern.struct.graph.Graph;
import me.bottdev.kern.struct.graph.MutableGraph;
import me.bottdev.kern.struct.graph.adjacency.AdjacencyListGraphBuilder;
import me.bottdev.kern.struct.algorithms.traverse.GraphTraversal;
import me.bottdev.kern.struct.algorithms.traverse.TraversalOrders;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class CommonGraphTests {

    private static GraphTraversal<String, EndpointPair<String>> dfs;
    private static GraphTraversal<String, EndpointPair<String>> bfs;

    @BeforeAll
    public static void setup() {
        dfs = new GraphTraversal<String, EndpointPair<String>>()
                .order(TraversalOrders.dfs());

        bfs = new GraphTraversal<String, EndpointPair<String>>()
                .order(TraversalOrders.bfs());

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



}
