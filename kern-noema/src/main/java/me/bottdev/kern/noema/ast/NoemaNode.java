package me.bottdev.kern.noema.ast;

import me.bottdev.kern.noema.token.Token;

public interface NoemaNode {
    Token token();

    void acceptVisitor(NodeVisitor visitor);

}
