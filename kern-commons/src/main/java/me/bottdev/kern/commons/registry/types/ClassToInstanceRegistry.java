package me.bottdev.kern.commons.registry.types;

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.MutableClassToInstanceMap;
import me.bottdev.kern.commons.registry.Registry;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class ClassToInstanceRegistry<T> implements Registry<Class<? extends T>, T> {

    private final ClassToInstanceMap<T> store;

    public ClassToInstanceRegistry() {
        store = MutableClassToInstanceMap.create();
    }

    @Override
    public boolean isRegistered(Class<? extends T> key) {
        return store.containsKey(key);
    }

    @Override
    public ClassToInstanceRegistry<T> register(Class<? extends T> key, T value) {
        if (store.containsKey(key)) {
            throw new IllegalStateException("Key already store: " + key.getName());
        }
        store.put(key, value);
        return this;
    }

    @Override
    public ClassToInstanceRegistry<T> registerIfAbsent(Class<? extends T> key, Function<Class<? extends T>, T> function) {
        if (store.containsKey(key)) return this;
        store.put(key, function.apply(key));
        return this;
    }

    @Override
    public ClassToInstanceRegistry<T> registerOrReplace(Class<? extends T> key, T value) {
        store.put(key, value);
        return this;
    }

    public ClassToInstanceRegistry<T> register(T value) {
        @SuppressWarnings("unchecked")
        Class<T> clazz = (Class<T>) value.getClass();
        register(clazz, value);
        return this;
    }

    @Override
    public T unregister(Class<? extends T> key) {
        return store.remove(key);
    }

    @Override
    public void clear() {
        store.clear();
    }

    @Override
    public T get(Class<? extends T> key) {
        return store.get(key);
    }

    @Override
    public Optional<T> find(Class<? extends T> key) {
        return Optional.ofNullable(store.getInstance(key));
    }

    @Override
    public Collection<T> getAll() {
        return store.values();
    }

    @Override
    public Map<Class<? extends T>, T> getMap() {
        return store;
    }

    @Override
    public int size() {
        return store.size();
    }

}