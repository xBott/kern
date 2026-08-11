package me.bottdev.kern.commons.wrapper;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/// Interface that used for objects that wrap other ones.
/// @param <T> Wrapped object.
public interface ObjectWrapper<T> {

    /// @return Indicates whether the wrapper contains a value.
    boolean isPresent();

    T unwrap();

    /// @return if wrapped value is present, returns wrapped value. Otherwise, returns a fallback value.
    T unwrapOr(T other);

    /// maps value inside the wrapper to another.
    <U> ObjectWrapper<U> map(Function<T, U> mapper);
    /// Maps the wrapper to another.
    <U, W extends ObjectWrapper<U>> W flatMap(Function<T, W> mapper);

    /// @return [Optional] with a wrapped value.
    Optional<T> toOptional();

    /// Executes a function that takes [T] as an argument if wrapped value is present.
    void ifPresent(Consumer<T> consumer);

}
