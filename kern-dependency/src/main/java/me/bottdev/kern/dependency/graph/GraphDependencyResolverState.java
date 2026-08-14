package me.bottdev.kern.dependency.graph;

import me.bottdev.kern.dependency.DependencyAware;
import me.bottdev.kern.dependency.DependencyResolverState;
import me.bottdev.kern.dependency.exceptions.ResolverForgetException;
import me.bottdev.kern.struct.graph.MutableGraph;
import me.bottdev.kern.struct.graph.adjacency.AdjacencyListGraphBuilder;
import me.bottdev.kern.struct.graph.endpoints.Directed;

import java.util.*;

public class GraphDependencyResolverState<K, T extends DependencyAware<K>> implements DependencyResolverState<K, T> {

    private final Map<K, T> committed = new HashMap<>();
    private final MutableGraph<K, Directed<K>> graph = new AdjacencyListGraphBuilder<K, Directed<K>>().mutable();

    @Override
    public boolean remembers(K key) {
        return committed.containsKey(key);
    }

    @Override
    public T get(K key) {
        return committed.get(key);
    }

    @Override
    public Collection<T> committed() {
        return Collections.unmodifiableCollection(committed.values());
    }

    @Override
    public Set<K> dependentsOf(K key) {
        return graph.predecessors(key);
    }

    @Override
    public void commit(T dependent) {
        K key = dependent.dependencyKey();
        if (committed.containsKey(key)) return;
        committed.put(key, dependent);
    }

    @Override
    public synchronized void forget(K key) {

        if (!committed.containsKey(key)) return;

        Set<K> dependents = dependentsOf(key);
        if (!dependents.isEmpty())
            throw new ResolverForgetException(
                    "Can't forget dependency \"" + key + "\", because " + dependents + " depend on it",
                    dependents
            );

        graph.removeNode(key);
        committed.remove(key);

    }

    public MutableGraph<K, Directed<K>> graph() {
        return graph;
    }

}
