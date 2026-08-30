package me.bottdev.kern.commons.registry.types;

import me.bottdev.kern.commons.registry.Registry;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class SimpleRegistry<K, T> implements Registry<K, T> {

    private final Map<K, T> store;

    public SimpleRegistry() {
        store = new ConcurrentHashMap<>();
    }

    public SimpleRegistry(Map<K, T> initialStore) {
        store = new ConcurrentHashMap<>(initialStore);
    }

    @Override
    public boolean isRegistered(K key) {
        return store.containsKey(key);
    }

    @Override
    public SimpleRegistry<K, T> register(K key, T value) {
        if (store.containsKey(key)) {
            throw new IllegalStateException("Key already registered: " + key);
        }
        store.put(key, value);
        return this;
    }

    @Override
    public SimpleRegistry<K, T> registerIfAbsent(K key, Function<K, T> function) {
        if (store.containsKey(key)) return this;
        store.put(key, function.apply(key));
        return this;
    }

    @Override
    public SimpleRegistry<K, T> registerOrReplace(K key, T value) {
        store.put(key, value);
        return this;
    }

    @Override
    public T unregister(K key) {
        return store.remove(key);
    }

    @Override
    public void clear() {
        store.clear();
    }

    @Override
    public T get(K key) {
        return store.get(key);
    }

    @Override
    public Optional<T> find(K key) {
        return Optional.ofNullable(store.get(key));
    }

    @Override
    public Collection<T> getAll() {
        return Collections.unmodifiableCollection(store.values());
    }

    @Override
    public Map<K, T> getMap() {
        return store;
    }

    @Override
    public int size() {
        return store.size();
    }
}
