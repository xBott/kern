package me.bottdev.kern.noema.ast.nodes;

import lombok.Builder;
import me.bottdev.kern.noema.ast.NodeVisitor;
import me.bottdev.kern.noema.ast.NoemaNode;
import me.bottdev.kern.noema.token.Token;

@Builder
public record KeyValueNode(
        Token token,
        String key,
        NoemaNode valueNode
) implements NoemaNode {

    @Override
    public void acceptVisitor(NodeVisitor visitor) {
        visitor.visitNode(this);
    }

}
