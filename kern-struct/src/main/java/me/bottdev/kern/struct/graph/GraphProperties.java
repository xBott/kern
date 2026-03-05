package me.bottdev.kern.struct.graph;

import me.bottdev.kern.struct.property.Property;

public class GraphProperties {

    public static final Property<Boolean> ALLOWS_PARALLEL_EDGES = Property.of("allows_parallel_edges", Boolean.class);
    public static final Property<Boolean> ALLOWS_SELF_LOOPS = Property.of("allows_self_loops", Boolean.class);

}
