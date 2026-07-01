package me.bottdev.kern.dependency.graph;

import lombok.Getter;
import me.bottdev.kern.dependency.DependencyAware;
import me.bottdev.kern.dependency.DependencyLink;
import me.bottdev.kern.struct.graph.endpoints.Directed;

public class DependencyEndpointPair<K, N extends DependencyAware<K>> extends Directed<N> {

    @Getter
    private final DependencyLink policy;

    public DependencyEndpointPair(N source, N target, DependencyLink policy) {
        super(source, target);
        this.policy = policy;
    }

}
