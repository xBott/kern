package me.bottdev.kern.struct.graph;

import me.bottdev.kern.struct.paths.CyclePath;
import me.bottdev.kern.struct.algorithms.cycle.SimpleCycleDetector;
import me.bottdev.kern.struct.graph.adjacency.AdjacencyListGraphBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Optional;

public class CycleDetectionTests {

    private static SimpleCycleDetector simpleCycleDetector;

    @BeforeAll
    static void setUp() {
        simpleCycleDetector = new SimpleCycleDetector();
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
        Optional<CyclePath<String>> optional = simpleCycleDetector.detect(graph);
        Assertions.assertTrue(optional.isPresent(), "detect should be present");

        System.out.println(optional.get());

    }


}
