package me.bottdev.kern.commons.registry.types;

import me.bottdev.kern.commons.key.TypedKey;
import me.bottdev.kern.commons.registry.TypedKeyRegistry;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/// Thread-safe, [ConcurrentHashMap]-backed implementation of [TypedRegistry].
///
/// Key identity is defined entirely by [TypedKey]: two keys are equal when
/// their `type()` and `qualifier()` are equal — the implementation
/// delegates to whatever `equals/hashCode` contract the supplied
/// [TypedKey] provides.
///
/// @param <T> the type of values stored in this registry
public class SimpleTypedKeyRegistry<T> implements TypedKeyRegistry<T> {

    private final Map<TypedKey<? extends T>, T> store = new ConcurrentHashMap<>();


    @Override
    public <S extends T> boolean isRegistered(TypedKey<S> key) {
        return store.containsKey(key);
    }

    @Override
    public <S extends T> SimpleTypedKeyRegistry<T> register(TypedKey<S> key, S value) {
        if (store.containsKey(key)) {
            throw new IllegalStateException("Key already registered: " + key);
        }
        store.put(key, value);
        return this;
    }

    @Override
    public <S extends T> SimpleTypedKeyRegistry<T> registerIfAbsent(TypedKey<S> key, Function<TypedKey<S>, S> function) {
        if (store.containsKey(key)) return this;
        store.put(key, function.apply(key));
        return this;
    }

    @Override
    public <S extends T> SimpleTypedKeyRegistry<T> registerOrReplace(TypedKey<S> key, S value) {
        store.put(key, value);
        return this;
    }

    @Override
    public <S extends T> T unregister(TypedKey<S> key) {
        return store.remove(key);
    }

    @Override
    public void clear() {
        store.clear();
    }

    @Override
    public <S extends T> S get(TypedKey<S> key) {
        T unknownValue = store.get(key);
        if (unknownValue == null) return null;
        return key.cast(unknownValue);
    }

    @Override
    public <S extends T> Optional<S> find(TypedKey<S> key) {
        T unknownValue = store.get(key);
        if (unknownValue == null) return Optional.empty();
        S value = key.cast(unknownValue);
        return Optional.of(value);
    }

    @Override
    public Collection<T> getAll() {
        return Collections.unmodifiableCollection(store.values());
    }

    @Override
    public Map<TypedKey<? extends T>, T> getMap() {
        return store;
    }

    @Override
    public int size() {
        return store.size();
    }

}