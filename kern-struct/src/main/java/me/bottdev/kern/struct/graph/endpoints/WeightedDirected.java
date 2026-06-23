package me.bottdev.kern.struct.graph.endpoints;

import lombok.Setter;
import me.bottdev.kern.struct.graph.Weighted;

public final class WeightedDirected<N> extends Directed<N> implements Weighted {

    @Setter
    private double weight;

    public WeightedDirected(N source, N target, double weight) {
        super(source, target);
        this.weight = weight;
    }

    @Override
    public double weight() {
        return weight;
    }

}

