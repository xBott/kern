package me.bottdev.kern.struct.graph;

import me.bottdev.kern.struct.graph.adjacency.AdjacencyListGraphBuilder;
import me.bottdev.kern.struct.algorithms.traverse.GraphTraversal;
import me.bottdev.kern.struct.algorithms.traverse.TraversalOrders;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

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
                .addEdge(EndpointPairs.directed("Node1", "Node3"))
                .addEdge(EndpointPairs.directed("Node3", "Node2"))
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
                .addEdge(EndpointPairs.directed("Node1", "Node3"))
                .addEdge(EndpointPairs.directed("Node3", "Node2"))
                .mutable();

        graph.addNode("Node4");
        graph.addNode("Node5");
        graph.addEdge(EndpointPairs.directed("Node1", "Node4"));
        graph.addEdge(EndpointPairs.directed("Node4", "Node2"));
        graph.addEdge(EndpointPairs.directed("Node4", "Node5"));

        Assertions.assertNotNull(graph, "Graph should not be null");
        Assertions.assertEquals(5, graph.nodeCount(), "Graph should have 5 nodes");
        Assertions.assertEquals(5, graph.edgeCount(), "Graph should have 5 edges");

        bfs.stream(graph, "Node1")
                .forEach(step -> {
                    System.out.println(step.node());
                });

    }

    @Test
    public void testNodeDegrees_ShouldReturnCorrectCount() {

        MutableGraph<String, EndpointPair<String>> graph = new AdjacencyListGraphBuilder<String, EndpointPair<String>>()
                .addEdge(EndpointPairs.directed("A", "B"))
                .addEdge(EndpointPairs.directed("A", "C"))
                .mutable();

        int outDegreeA = graph.outDegree("A");
        int inDegreeB = graph.inDegree("B");
        int inDegreeA = graph.inDegree("A");

        Assertions.assertEquals(2, outDegreeA, "У узла A должно быть 2 исходящих ребра");
        Assertions.assertEquals(1, inDegreeB, "У узла B должно быть 1 входящее ребро");
        Assertions.assertEquals(0, inDegreeA, "У узла A не должно быть входящих ребер");
    }

    @Test
    public void testNodeDegrees_ShouldReturnCorrectDegrees() {

        MutableGraph<String, EndpointPair<String>> graph = new AdjacencyListGraphBuilder<String, EndpointPair<String>>()
                .addEdge(EndpointPairs.directed("A", "B"))
                .addEdge(EndpointPairs.directed("B", "C"))
                .mutable();

        int inDegreeA = graph.inDegree("A");
        int inDegreeB = graph.inDegree("B");
        int inDegreeC = graph.inDegree("C");

        int outDegreeA = graph.outDegree("A");
        int outDegreeB = graph.outDegree("B");
        int outDegreeC = graph.outDegree("C");

        Assertions.assertEquals(0, inDegreeA, "У узла A должно быть 0 входящих ребер");
        Assertions.assertEquals(1, inDegreeB, "У узла B должно быть 1 входящее ребро");
        Assertions.assertEquals(1, inDegreeC, "У узла C должно быть 1 входящее ребро");

        Assertions.assertEquals(1, outDegreeA, "У узла A должно быть 1 исходящее ребер");
        Assertions.assertEquals(1, outDegreeB, "У узла B должно быть 1 исходящее ребро");
        Assertions.assertEquals(0, outDegreeC, "У узла C должно быть 0 исходящих ребер");
    }

    @Test
    public void testSuccessorsAndPredecessors_ShouldIdentifyCorrectNodes() {

        Graph<String, EndpointPair<String>> graph = new AdjacencyListGraphBuilder<String, EndpointPair<String>>()
                .addEdge(EndpointPairs.directed("X", "Y"))
                .immutable();

        Set<String> successorsX = graph.successors("X");
        Set<String> predecessorsY = graph.predecessors("Y");

        Assertions.assertTrue(successorsX.contains("Y"), "Y должен быть наследником X");
        Assertions.assertTrue(predecessorsY.contains("X"), "X должен быть предшественником Y");
        Assertions.assertFalse(successorsX.contains("X"), "X не должен быть наследником самого себя без петли");
    }

    @Test
    public void testHasEdgeConnecting_WithNonExistentNodes_ShouldReturnFalse() {
        Graph<String, EndpointPair<String>> graph = new AdjacencyListGraphBuilder<String, EndpointPair<String>>()
                .addNode("A")
                .immutable();

        boolean connectionExists = graph.hasEdgeConnecting("A", "B");

        Assertions.assertFalse(connectionExists, "Связь с несуществующим узлом B не должна существовать");
    }



}
