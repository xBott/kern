package me.bottdev.kern.noema.ast;

import java.util.List;

public interface ChildHolder<T extends NoemaNode> {

    List<? extends T> children();

}
