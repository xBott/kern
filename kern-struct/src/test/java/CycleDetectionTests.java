import me.bottdev.kern.struct.algorithms.cycle.CycleDetector;
import me.bottdev.kern.struct.algorithms.cycle.CyclePath;
import me.bottdev.kern.struct.algorithms.cycle.CycleResult;
import me.bottdev.kern.struct.graph.EndpointPair;
import me.bottdev.kern.struct.graph.Graph;
import me.bottdev.kern.struct.graph.adjacency.AdjacencyListGraphBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Optional;

public class CycleDetectionTests {

    private static CycleDetector<String, String> cycleDetector;

    @BeforeAll
    static void setUp() {
        cycleDetector = CycleDetector.identityNormalizer();
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
