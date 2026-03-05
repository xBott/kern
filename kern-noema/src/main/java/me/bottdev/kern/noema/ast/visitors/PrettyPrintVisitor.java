package me.bottdev.kern.noema.ast.visitors;

import me.bottdev.kern.noema.ast.NoemaNode;
import me.bottdev.kern.noema.ast.nodes.KeyValueNode;
import me.bottdev.kern.noema.ast.nodes.LeafValueNode;
import me.bottdev.kern.noema.ast.nodes.ListNode;
import me.bottdev.kern.noema.ast.nodes.SectionNode;

public class PrettyPrintVisitor extends TypedVisitor<String> {

    private final StringBuilder sb = new StringBuilder();
    private int indent = 0;

    private String indent() {
        return "  ".repeat(indent);
    }

    public PrettyPrintVisitor() {


    }

    public String getResult() {
        return sb.toString();
    }

}