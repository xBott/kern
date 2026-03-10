package me.bottdev.kern.struct;

import java.util.List;

public interface PathResult<N> {

    N start();
    N target();
    List<N> nodes();

}
