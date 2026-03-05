package me.bottdev.kern.struct.graph;

import me.bottdev.kern.struct.property.PropertyStore;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractGraph<N, E extends EndpointPair<N>> implements Graph<N, E> {

    private final PropertyStore propertyStore;

    public AbstractGraph(PropertyStore propertyStore) {
        this.propertyStore = propertyStore;
    }

    @Override
    public PropertyStore propertyStore() {
        return propertyStore;
    }

    @Override
    public Set<N> successors(N node) {
        return incidentEdges(node).stream()
                .flatMap(edge -> edge.reachableFrom(node).stream())
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public Set<N> predecessors(N node) {
        return incidentEdges(node).stream()
                .flatMap(edge -> {
                    N adjacentNode = edge.adjacentNode(node);
                    return edge.reachableFrom(adjacentNode).stream();
                })
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public Set<N> adjacentNodes(N node) {
       Set<N> adjacentNodes = new LinkedHashSet<>();
       adjacentNodes.addAll(successors(node));
       adjacentNodes.addAll(predecessors(node));
       return Collections.unmodifiableSet(adjacentNodes);
    }

    @Override
    public Set<E> outEdges(N node) {
        return incidentEdges(node).stream()
                .filter(edge -> edge.reachableFrom(node).isPresent())
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public Set<E> inEdges(N node) {
        return incidentEdges(node).stream()
                .filter(edge -> {
                    N adjacentNode = edge.adjacentNode(node);
                    return edge.reachableFrom(adjacentNode).isPresent();
                })
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public int degree(N node) {
        return incidentEdges(node).size();
    }

    @Override
    public int inDegree(N node) {
        return inEdges(node).size();
    }

    @Override
    public int outDegree(N node) {
        return outEdges(node).size();
    }

    @Override
    public boolean hasNode(N node) {
        return nodes().contains(node);
    }

    @Override
    public boolean hasEdgeConnecting(N nodeU, N nodeV) {
        if (!hasNode(nodeU)) return false;
        return incidentEdges(nodeU).stream()
                .anyMatch(edge -> edge.hasEndpoint(nodeV));
    }

}
