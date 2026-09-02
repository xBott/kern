package me.bottdev.kern.meta.core.models;

import java.util.*;

public class ModelRegistry {

    private final Map<ModelKind<?>, Map<String, Model>> byKindAndKey = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <M extends Model> M register(ModelKind<M> kind, Model rawModel) {
        M typed = kind.cast(rawModel);
        String key = kind.keyOf(typed);
        Map<String, Model> byKey = byKindAndKey.computeIfAbsent(kind, k -> new LinkedHashMap<>());
        Model existing = byKey.computeIfAbsent(key, k -> typed);
        return (M) existing;
    }

    public <M extends Model> ModelIndex<M> indexOf(ModelKind<M> kind) {
        @SuppressWarnings("unchecked")
        Collection<M> models = (Collection<M>) byKindAndKey.getOrDefault(kind, Map.of()).values();
        return ModelIndex.of(kind, List.copyOf(models), kind::keyOf);
    }

}
