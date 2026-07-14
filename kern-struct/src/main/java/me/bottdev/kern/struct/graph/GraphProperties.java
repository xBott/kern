package me.bottdev.kern.struct.graph;

import me.bottdev.kern.struct.property.Property;

/// Standard property keys used by graph implementations.
public class GraphProperties {

    /// Property that controls whether parallel edges are allowed.
    public static final Property<Boolean> ALLOWS_PARALLEL_EDGES = Property.of("allows_parallel_edges", Boolean.class);

    /// Property that controls whether self-loop edges are allowed.
    public static final Property<Boolean> ALLOWS_SELF_LOOPS = Property.of("allows_self_loops", Boolean.class);

}
