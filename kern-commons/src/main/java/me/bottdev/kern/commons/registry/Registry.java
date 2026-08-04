package me.bottdev.kern.commons.registry;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public interface Registry<K, T> {

    boolean isRegistered(K key);

    Registry<K, T> register(K key, T value);

    Registry<K, T> registerIfAbsent(K key, Function<K, T> function);

    Registry<K, T> registerOrReplace(K key, T value);

    T unregister(K key);

    void clear();

    T get(K key);

    Optional<T> find(K key);

    Collection<T> getAll();

    Map<K, T> getMap();

    int size();

}
