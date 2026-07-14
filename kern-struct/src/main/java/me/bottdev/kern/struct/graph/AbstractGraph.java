package me.bottdev.kern.struct.graph;

import me.bottdev.kern.struct.property.PropertyStore;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

/// Base implementation of common graph queries.
///
/// @param <N> node type
/// @param <E> edge endpoint type
public abstract class AbstractGraph<N, E extends EndpointPair<N>> implements Graph<N, E> {

    private final PropertyStore propertyStore;

    /// Creates a graph with the provided property store.
    ///
    /// @param propertyStore graph property store
    public AbstractGraph(PropertyStore propertyStore) {
        this.propertyStore = propertyStore;
    }

    @Override
    public int nodeCount() {
        return nodes().size();
    }

    @Override
    public int edgeCount() {
        return edges().size();
    }

    @Override
    public boolean isEmpty() {
        return nodes().isEmpty();
    }

    @Override
    public PropertyStore propertyStore() {
        return propertyStore;
    }

    @Override
    public Set<N> roots() {
        Set<N> result = new LinkedHashSet<>();
        for (N node : nodes()) {
            if (predecessors(node).isEmpty()) {
                result.add(node);
            }
        }
        return Collections.unmodifiableSet(result);
    }

    @Override
    public Set<N> leaves() {
        Set<N> result = new LinkedHashSet<>();
        for (N node : nodes()) {
            if (successors(node).isEmpty()) {
                result.add(node);
            }
        }
        return Collections.unmodifiableSet(result);
    }

    @Override
    public Set<N> successors(N node) {
        Set<N> successors = new LinkedHashSet<>();
        for (E edge : incidentEdges(node)) {
            edge.reachableFrom(node).ifPresent(successors::add);
        }
        return Collections.unmodifiableSet(successors);
    }

    @Override
    public Set<N> predecessors(N node) {
        Set<N> predecessors = new LinkedHashSet<>();
        for (E edge : incidentEdges(node)) {
            N source = edge.adjacentNode(node);
            if (edge.reachableFrom(source).filter(node::equals).isPresent()) {
                predecessors.add(source);
            }
        }
        return Collections.unmodifiableSet(predecessors);
    }

    @Override
    public Set<N> adjacentNodes(N node) {
        Set<N> adjacentNodes = new LinkedHashSet<>();
        for (E edge : incidentEdges(node)) {
            adjacentNodes.add(edge.adjacentNode(node));
        }
        return Collections.unmodifiableSet(adjacentNodes);
    }

    @Override
    public Set<E> outEdges(N node) {
        Set<E> outEdges = new LinkedHashSet<>();
        for (E edge : incidentEdges(node)) {
            if (edge.reachableFrom(node).isPresent()) {
                outEdges.add(edge);
            }
        }
        return Collections.unmodifiableSet(outEdges);
    }

    @Override
    public Set<E> inEdges(N node) {
        Set<E> inEdges = new LinkedHashSet<>();
        for (E edge : incidentEdges(node)) {
            if (edge.reachableFrom(edge.adjacentNode(node)).isPresent()) {
                inEdges.add(edge);
            }
        }
        return Collections.unmodifiableSet(inEdges);
    }

    @Override
    public int degree(N node) {
        return incidentEdges(node).size();
    }

    @Override
    public int inDegree(N node) {
        int degree = 0;
        for (E edge : incidentEdges(node)) {
            if (edge.reachableFrom(edge.adjacentNode(node)).isPresent()) {
                degree++;
            }
        }
        return degree;
    }

    @Override
    public int outDegree(N node) {
        int degree = 0;
        for (E edge : incidentEdges(node)) {
            if (edge.reachableFrom(node).isPresent()) {
                degree++;
            }
        }
        return degree;
    }

    @Override
    public Set<E> edgesConnecting(N nodeU, N nodeV) {

        Set<E> edgesConnecting = new LinkedHashSet<>();

        for (E edge : incidentEdges(nodeU)) {
            if (edgeConnects(edge, nodeU, nodeV)) edgesConnecting.add(edge);
        }

        return Collections.unmodifiableSet(edgesConnecting);

    }

    @Override
    public Optional<E> edgeConnecting(N nodeU, N nodeV) {

        for (E edge : incidentEdges(nodeU)) {
            if (edgeConnects(edge, nodeU, nodeV)) return Optional.of(edge);
        }

        return Optional.empty();
    }

    private boolean edgeConnects(E edge, N from, N to) {
        if (edge.isDirected()) {
            return edge.nodeU().equals(from) && edge.nodeV().equals(to);
        } else {
            return edge.hasEndpoint(from) && edge.hasEndpoint(to);
        }
    }

}
