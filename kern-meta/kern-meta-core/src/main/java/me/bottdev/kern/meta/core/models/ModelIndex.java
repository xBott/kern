package me.bottdev.kern.meta.core.models;

import lombok.RequiredArgsConstructor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public final class ModelIndex<M extends Model> {

    private final ModelKind<M> kind;
    private final Map<String, M> byKey;

    static <M extends Model> ModelIndex<M> of(ModelKind<M> kind, List<M> models, Function<M, String> keyOf) {
        return new ModelIndex<>(kind, models.stream()
                .collect(Collectors.toMap(keyOf, m -> m, (a, b) -> a, LinkedHashMap::new)));
    }

    public Optional<M> resolve(ModelRef<M> ref) {
        if (ref.kind() != kind) {
            throw new IllegalArgumentException("ModelIndex<" + kind + "> получил ModelRef другого вида: " + ref.kind());
        }
        return Optional.ofNullable(byKey.get(ref.type().qualifiedName()));
    }

    public List<M> all() {
        return List.copyOf(byKey.values());
    }

}