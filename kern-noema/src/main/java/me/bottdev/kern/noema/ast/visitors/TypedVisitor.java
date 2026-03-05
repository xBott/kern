package me.bottdev.kern.noema.ast.visitors;

import me.bottdev.kern.noema.ast.NodeVisitor;
import me.bottdev.kern.noema.ast.NoemaNode;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TypedVisitor<T> implements NodeVisitor<T> {

    @FunctionalInterface
    public interface VisitorFunction<T, N extends NoemaNode> {

        T apply(N node);

    }

    private final Map<Class<? extends NoemaNode>, VisitorFunction<T, ?>> nodes = new HashMap<>();

    public <N extends NoemaNode> void registerType(Class<N> type, VisitorFunction<T, N> function) {
        nodes.put(type, function);
    }
    
    private Optional<VisitorFunction<T, ?>> findFunction(Class<? extends NoemaNode> type) {
        return Optional.ofNullable(nodes.get(type));
    }

    @SuppressWarnings("unchecked")
    private <N extends NoemaNode> T dispatch(VisitorFunction<T, N> function, NoemaNode node) {
        return function.apply((N) node);
    }

    @Override
    public T visitNode(NoemaNode node) {
         return findFunction(node.getClass())
                 .map(function -> dispatch(function, node))
                 .orElse(null);
    }

}
