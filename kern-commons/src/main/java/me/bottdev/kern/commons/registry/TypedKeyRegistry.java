package me.bottdev.kern.commons.registry;

import me.bottdev.kern.commons.key.TypedKey;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/// A type-safe registry keyed by [TypedKey].
///
/// The generic parameter `T` constrains both the key and the stored value
/// to the same type, providing compile-time safety on top of the raw
/// [Registry] contract.
///
/// @param <T> the type of values stored in this registry
public interface TypedKeyRegistry<T> {

    <S extends T> boolean isRegistered(TypedKey<S> key);

    <S extends T> TypedKeyRegistry<T> register(TypedKey<S> key, S value);

    <S extends T> TypedKeyRegistry<T> registerIfAbsent(TypedKey<S> key, Function<TypedKey<S>, S> function);

    <S extends T> TypedKeyRegistry<T> registerOrReplace(TypedKey<S> key, S value);

    <S extends T>  T unregister(TypedKey<S> key);

    void clear();

    <S extends T> S get(TypedKey<S> key);

    <S extends T> Optional<S> find(TypedKey<S> key);

    Collection<T> getAll();

    Map<TypedKey<? extends T>, T> getMap();

    int size();

}
