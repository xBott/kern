package me.bottdev.kern.noema.ast.nodes;

import lombok.Builder;
import lombok.Singular;
import me.bottdev.kern.noema.ast.ChildHolder;
import me.bottdev.kern.noema.ast.NodeVisitor;
import me.bottdev.kern.noema.ast.NoemaNode;
import me.bottdev.kern.noema.token.Token;

import java.util.List;

@Builder
public record ListNode(
        Token token,
        @Singular List<NoemaNode> children
) implements NoemaNode, ChildHolder<NoemaNode> {

    @Override
    public void acceptVisitor(NodeVisitor visitor) {
        visitor.visitNode(this);
    }

}
