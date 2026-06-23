package me.bottdev.kern.struct.graph;

import me.bottdev.kern.struct.algorithms.cycle.CyclePath;
import me.bottdev.kern.struct.algorithms.cycle.CycleResult;
import me.bottdev.kern.struct.algorithms.cycle.PreciseCycleDetector;
import me.bottdev.kern.struct.algorithms.cycle.SimpleCycleDetector;
import me.bottdev.kern.struct.graph.adjacency.AdjacencyListGraphBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Optional;

public class CycleDetectionTests {

    private static SimpleCycleDetector<String> simpleCycleDetector;
    private static PreciseCycleDetector<String, String> preciseCycleDetector;

    @BeforeAll
    static void setUp() {
        simpleCycleDetector = new SimpleCycleDetector<>();
        preciseCycleDetector = PreciseCycleDetector.identityNormalizer();
    }

    @Test
    public void testGraphSimpleCycleDetection() {

        System.out.println("Test graph simple cycle detection: ");

        Graph<String, EndpointPair<String>> graph = new AdjacencyListGraphBuilder<String, EndpointPair<String>>()
                .addNode("Node1")
                .addNode("Node2")
                .addNode("Node3")
                .addNode("Node4")
                .addNode("Node5")
                .addEdge(EndpointPairs.directed("Node1", "Node2"))
                .addEdge(EndpointPairs.directed("Node2", "Node3"))
                .addEdge(EndpointPairs.directed("Node3", "Node4"))
                .addEdge(EndpointPairs.directed("Node4", "Node5"))
                .addEdge(EndpointPairs.directed("Node5", "Node1"))
                .immutable();

        Assertions.assertNotNull(graph, "Graph should not be null");
        Assertions.assertEquals(5, graph.nodeCount(), "Graph should have 5 nodes");
        Assertions.assertEquals(5, graph.edgeCount(), "Graph should have 5 edges");

        //Test detect first
        Optional<CyclePath<String>> optional = simpleCycleDetector.detectFirst(graph);
        Assertions.assertTrue(optional.isPresent(), "detectFirst should be present");

        System.out.println(optional.get());

        //Test detect all
        CycleResult<String> result = simpleCycleDetector.detectAll(graph);
        Assertions.assertEquals(1, result.amount(), "detectAll should detect 1 unique cycle");

        CyclePath<String> first = result.cycles().iterator().next();
        Assertions.assertEquals(first, optional.get(), "Cycle from detectFirst should equal first cycle from detectAll");

        System.out.println(result);

    }

    @Test
    public void testGraphPreciseCycleDetection() {

        System.out.println("Test graph precise cycle detection: ");

        Graph<String, EndpointPair<String>> graph = new AdjacencyListGraphBuilder<String, EndpointPair<String>>()
                .addNode("Node1")
                .addNode("Node2")
                .addNode("Node3")
                .addNode("Node4")
                .addNode("Node5")
                .addEdge(EndpointPairs.directed("Node1", "Node2"))
                .addEdge(EndpointPairs.directed("Node2", "Node3"))
                .addEdge(EndpointPairs.directed("Node3", "Node4"))
                .addEdge(EndpointPairs.directed("Node4", "Node5"))
                .addEdge(EndpointPairs.directed("Node5", "Node1"))
                .immutable();

        Assertions.assertNotNull(graph, "Graph should not be null");
        Assertions.assertEquals(5, graph.nodeCount(), "Graph should have 5 nodes");
        Assertions.assertEquals(5, graph.edgeCount(), "Graph should have 5 edges");

        //Test detect first
        Optional<CyclePath<String>> optional = preciseCycleDetector.detectFirst(graph);
        Assertions.assertTrue(optional.isPresent(), "detectFirst should be present");

        System.out.println(optional.get());

        //Test detect all
        CycleResult<String> result = preciseCycleDetector.detectAll(graph);
        Assertions.assertEquals(1, result.amount(), "detectAll should detect 1 unique cycle");

        CyclePath<String> first = result.cycles().iterator().next();
        Assertions.assertEquals(first, optional.get(), "Cycle from detectFirst should equal first cycle from detectAll");

        System.out.println(result);

    }


}
