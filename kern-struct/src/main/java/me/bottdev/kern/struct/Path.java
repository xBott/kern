package me.bottdev.kern.struct;

import java.util.List;

public interface Path<N> {

    N start();

    N target();

    List<N> nodes();

}
