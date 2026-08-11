package me.bottdev.kern.commons.wrapper;

import org.jspecify.annotations.NonNull;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/// Represents the result of an operation, containing either a success value [T] or an error [E].
///
/// @param <T> The type of the success value.
/// @param <E> The type of the error value.
public sealed interface Result<T, E> extends ObjectWrapper<T> permits
        Result.Ok,
        Result.Error
{

    /// @return Indicates whether the result represents a successful operation.
    default boolean isOk() {
        return isPresent();
    }

    /// @return Indicates whether the result represents a failed operation.
    boolean isError();

    /// @return The error value if present.
    E unwrapError();

    /// Maps the error inside the wrapper to another type.
    <F> Result<T, F> mapError(Function<? super E, ? extends F> mapper);

    /// Folds the result into a single value by applying one of two functions depending on the state.
    ///
    /// @param okFn  The function to apply if the result is successful.
    /// @param errFn The function to apply if the result is an error.
    /// @param <U>   The return type.
    /// @return The result of applying the respective function.
    <U> U fold(Function<? super T, U> okFn, Function<? super E, U> errFn);

    /// Executes a consumer if the result is successful.
    void ifOk(Consumer<T> consumer);

    /// Executes a consumer if the result is an error.
    void ifError(Consumer<E> consumer);

    /// @return A new successful [Result].
    static <T, E> Result<T, E> ok(@NonNull T value) {
        return new Ok<>(value);
    }

    /// @return A new failed [Result].
    static <T, E> Result<T, E> error(E error) {
        return new Error<>(error);
    }

    /// Represents a successful [Result] containing a value [T].
    record Ok<T, E>(@NonNull T value) implements Result<T, E> {

        @Override
        public boolean isPresent() {
            return true;
        }

        @Override
        public boolean isOk() {
            return true;
        }

        @Override
        public boolean isError() {
            return false;
        }

        @Override
        public T unwrap() {
            return value;
        }

        @Override
        public E unwrapError() {
            throw new IllegalStateException("Called unwrapError on Ok result");
        }

        @Override
        public T unwrapOr(T other) {
            return value;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <U> Result<U, E> map(Function<T, U> mapper) {
            return new Ok<>(mapper.apply(value));
        }

        @Override
        @SuppressWarnings("unchecked")
        public <U, W extends ObjectWrapper<U>> W flatMap(Function<T, W> mapper) {
            return mapper.apply(value);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <F> Result<T, F> mapError(Function<? super E, ? extends F> mapper) {
            return (Result<T, F>) this;
        }

        @Override
        public <U> U fold(Function<? super T, U> okFn, Function<? super E, U> errFn) {
            return okFn.apply(value);
        }

        @Override
        public Optional<T> toOptional() {
            return Optional.of(value);
        }

        @Override
        public void ifPresent(Consumer<T> consumer) {
            consumer.accept(value);
        }

        @Override
        public void ifOk(Consumer<T> consumer) {
            consumer.accept(value);
        }

        @Override
        public void ifError(Consumer<E> consumer) {
        }

    }

    /// Represents a failed [Result] containing an error [E].
    record Error<T, E>(E error) implements Result<T, E> {

        @Override
        public boolean isPresent() {
            return false;
        }

        @Override
        public boolean isOk() {
            return false;
        }

        @Override
        public boolean isError() {
            return true;
        }

        @Override
        public T unwrap() {
            throw new IllegalStateException("Called unwrap on Error result: " + error);
        }

        @Override
        public E unwrapError() {
            return error;
        }

        @Override
        public T unwrapOr(T other) {
            return other;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <U> Result<U, E> map(Function<T, U> mapper) {
            return (Result<U, E>) this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <U, W extends ObjectWrapper<U>> W flatMap(Function<T, W> mapper) {
            return (W) this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <F> Result<T, F> mapError(Function<? super E, ? extends F> mapper) {
            return new Error<>(mapper.apply(error));
        }

        @Override
        public <U> U fold(Function<? super T, U> okFn, Function<? super E, U> errFn) {
            return errFn.apply(error);
        }

        @Override
        public Optional<T> toOptional() {
            return Optional.empty();
        }

        @Override
        public void ifPresent(Consumer<T> consumer) {
        }

        @Override
        public void ifOk(Consumer<T> consumer) {
        }

        @Override
        public void ifError(Consumer<E> consumer) {
            consumer.accept(error);
        }
    }

}