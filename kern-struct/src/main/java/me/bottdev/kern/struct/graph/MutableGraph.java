package me.bottdev.kern.struct.graph;

import me.bottdev.kern.struct.graph.exceptions.GraphParallelEdgeException;
import me.bottdev.kern.struct.graph.exceptions.GraphSelfLoopException;

import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Mutable graph API.
 *
 * <p>Node operations are idempotent. Edge operations validate graph invariants and throw
 * when self-loop or parallel-edge rules are violated.</p>
 *
 * @param <N> node type
 * @param <E> edge endpoint type
 */
public interface MutableGraph<N, E extends EndpointPair<N>> extends Graph<N, E> {

    /**
     * Adds a node.
     *
     * @param node node to add
     * @return true if the node was added, false if it already existed
     */
    boolean addNode(N node);

    /**
     * Removes a node and all incident edges.
     *
     * @param node node to remove
     * @return true if the node was removed
     */
    boolean removeNode(N node);

    /**
     * Adds an edge.
     *
     * @param edge edge to add
     * @throws GraphSelfLoopException if the edge is a self-loop and self-loops are disabled
     * @throws GraphParallelEdgeException if a parallel edge exists and parallel edges are disabled
     */
    void addEdge(E edge);

    /**
     * Adds an edge if it is not already present.
     *
     * @param edge edge to add
     * @return true if the edge was added
     */
    default boolean addEdgeIfAbsent(E edge) {
        if (hasEdge(edge)) return false;
        addEdge(edge);
        return true;
    }

    /**
     * Replaces one edge with another.
     *
     * @param oldEdge edge to remove
     * @param newEdge edge to add
     * @return true if the old edge existed and was replaced
     */
    default boolean replaceEdge(E oldEdge, E newEdge) {
        if (!removeEdge(oldEdge)) return false;
        addEdge(newEdge);
        return true;
    }

    /**
     * Removes an edge.
     *
     * @param edge edge to remove
     * @return true if the edge was removed
     */
    boolean removeEdge(E edge);

    /**
     * Removes all edges connecting the given nodes.
     *
     * @param nodeU first node
     * @param nodeV second node
     * @return true if at least one edge was removed
     */
    default boolean removeEdgesConnecting(N nodeU, N nodeV) {
        Set<E> toRemove = edgesConnecting(nodeU, nodeV);
        boolean anyRemoved = false;
        for (E edge : toRemove) {
            anyRemoved |= removeEdge(edge);
        }
        return anyRemoved;
    }

    /**
     * Adds all nodes from the collection.
     *
     * @param nodes nodes to add
     * @return number of nodes that were actually added
     */
    int addAllNodes(Collection<N> nodes);

    /**
     * Adds all edges as an atomic batch.
     *
     * @param edges edges to add
     * @return number of edges that were actually added
     * @throws GraphSelfLoopException if an edge is a self-loop and self-loops are disabled
     * @throws GraphParallelEdgeException if a parallel edge exists and parallel edges are disabled
     */
    int addAllEdges(Collection<E> edges);

    /**
     * Removes all provided nodes.
     *
     * @param nodes nodes to remove
     * @return number of nodes that were removed
     */
    int removeAllNodes(Collection<N> nodes);

    /**
     * Removes all provided edges.
     *
     * @param edges edges to remove
     * @return number of edges that were removed
     */
    int removeAllEdges(Collection<E> edges);

    /**
     * Removes all nodes and edges.
     */
    void clear();

    /**
     * Returns an immutable snapshot of the current graph.
     *
     * @return immutable graph snapshot
     */
    Graph<N, E> toImmutable();

    /**
     * Returns a deep mutable copy of this graph.
     *
     * @return mutable graph copy
     */
    MutableGraph<N, E> copy();

    @Override
    MutableGraph<N, E> subgraph(Set<N> subNodes);

    @Override
    MutableGraph<N, E> filterNodes(Predicate<N> predicate);

    @Override
    MutableGraph<N, E> filterEdges(Predicate<E> predicate);

}
