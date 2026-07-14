package me.bottdev.kern.struct.graph;

import me.bottdev.kern.struct.NeighborProvider;
import me.bottdev.kern.struct.property.PropertyHolder;

import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

/// Read-only graph API.
///
/// @param <N> node type
/// @param <E> edge endpoint type
public interface Graph<N, E extends EndpointPair<N>> extends PropertyHolder, NeighborProvider<N> {

    /// Returns all nodes in this graph.
    ///
    /// @return all graph nodes
    Set<N> nodes();

    /// Returns all edges in this graph.
    ///
    /// @return all graph edges
    Set<E> edges();

    /// Returns the number of nodes.
    ///
    /// @return node count
    int nodeCount();

    /// Returns the number of edges.
    ///
    /// @return edge count
    int edgeCount();

    /// Returns true when this graph has no nodes.
    ///
    /// @return true if the graph is empty
    boolean isEmpty();

    /// Returns all nodes connected to the given node, ignoring edge direction.
    ///
    /// @param node node to inspect
    /// @return adjacent nodes
    Set<N> adjacentNodes(N node);

    /// Returns nodes reachable from the given node.
    ///
    /// @param node source node
    /// @return successor nodes
    Set<N> successors(N node);

    /// Returns nodes that can reach the given node.
    ///
    /// @param node target node
    /// @return predecessor nodes
    Set<N> predecessors(N node);

    /// Returns nodes without predecessors.
    ///
    /// @return root nodes
    Set<N> roots();

    /// Returns nodes without successors.
    ///
    /// @return leaf nodes
    Set<N> leaves();

    /// Returns all edges touching the given node.
    ///
    /// @param node node to inspect
    /// @return incident edges
    Set<E> incidentEdges(N node);

    /// Returns edges that can be traversed from the given node.
    ///
    /// @param node source node
    /// @return outgoing edges
    Set<E> outEdges(N node);

    /// Returns edges that can be traversed into the given node.
    ///
    /// @param node target node
    /// @return incoming edges
    Set<E> inEdges(N node);

    /// Returns the number of incident edges.
    ///
    /// @param node node to inspect
    /// @return incident edge count
    int degree(N node);

    /// Returns the number of outgoing edges.
    ///
    /// @param node node to inspect
    /// @return outgoing edge count
    int outDegree(N node);

    /// Returns the number of incoming edges.
    ///
    /// @param node node to inspect
    /// @return incoming edge count
    int inDegree(N node);

    /// Returns true if the graph contains the node.
    ///
    /// @param node node to check
    /// @return true if the node exists
    boolean hasNode(N node);

    /// Returns true if the graph contains the edge.
    ///
    /// @param edge edge to check
    /// @return true if the edge exists
    boolean hasEdge(E edge);

    /// Returns true if at least one edge connects `nodeU` to `nodeV`.
    ///
    /// @param nodeU source node for directed edges
    /// @param nodeV target node for directed edges
    /// @return true if a connecting edge exists
    boolean hasEdgeConnecting(N nodeU, N nodeV);

    /// Returns all edges connecting `nodeU` to `nodeV`.
    ///
    /// @param nodeU source node for directed edges
    /// @param nodeV target node for directed edges
    /// @return connecting edges
    Set<E> edgesConnecting(N nodeU, N nodeV);

    /// Returns any edge connecting `nodeU` to `nodeV`.
    ///
    /// @param nodeU source node for directed edges
    /// @param nodeV target node for directed edges
    /// @return optional connecting edge
    Optional<E> edgeConnecting(N nodeU, N nodeV);

    /// Returns true if this graph allows more than one edge between the same nodes.
    ///
    /// @return true if parallel edges are allowed
    default boolean allowsParallelEdges() {
        return getProperty(GraphProperties.ALLOWS_PARALLEL_EDGES);
    }

    /// Returns true if this graph allows edges from a node to itself.
    ///
    /// @return true if self-loops are allowed
    default boolean allowsSelfLoops() {
        return getProperty(GraphProperties.ALLOWS_SELF_LOOPS);
    }

    /// Returns a mutable copy of this graph.
    ///
    /// @return mutable graph copy
    MutableGraph<N, E> toMutable();

    /// Returns the induced subgraph for the provided nodes.
    ///
    /// @param subNodes nodes to include
    /// @return induced subgraph
    /// @throws java.util.NoSuchElementException if a requested node is absent
    Graph<N, E> subgraph(Set<N> subNodes);

    /// Returns a graph containing only nodes accepted by the predicate.
    ///
    /// @param predicate node predicate
    /// @return filtered graph
    Graph<N, E> filterNodes(Predicate<N> predicate);

    /// Returns a graph containing only edges accepted by the predicate.
    ///
    /// @param predicate edge predicate
    /// @return filtered graph
    Graph<N, E> filterEdges(Predicate<E> predicate);

    @Override
    default Iterable<N> neighbors(N node) {
        return successors(node);
    }

}
