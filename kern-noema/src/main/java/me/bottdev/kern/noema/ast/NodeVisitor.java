package me.bottdev.kern.noema.ast;

public interface NodeVisitor<T> {

    T visitNode(NoemaNode node);

}
