package me.bottdev.kern.commons;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public sealed interface Result<T, E> permits Result.Ok, Result.Err {

    boolean isOk();
    boolean isError();

    T unwrap();
    E unwrapError();

    T unwrapOr(T other);

    <U> Result<U, E> map(Function<? super T, ? extends U> mapper);

    <F> Result<T, F> mapError(Function<? super E, ? extends F> mapper);

    <U> Result<U, E> flatMap(Function<? super T, Result<U, E>> mapper);

    <U> U fold(Function<? super T, U> okFn, Function<? super E, U> errFn);

    Optional<T> toOptional();

    void ifOk(Consumer<T> consumer);
    void ifError(Consumer<E> consumer);

    static <T,E> Result<T,E> ok(T value) {
        return new Ok<>(value);
    }

    static <T,E> Result<T,E> err(E error) {
        return new Err<>(error);
    }

    record Ok<T,E>(T value) implements Result<T,E> {

        public boolean isOk() { return true; }
        public boolean isError() { return false; }

        public T unwrap() { return value; }

        public E unwrapError() {
            throw new IllegalStateException("Called unwrapErr on Ok");
        }

        public T unwrapOr(T other) { return value; }

        public <U> Result<U,E> map(Function<? super T, ? extends U> mapper) {
            return ok(mapper.apply(value));
        }

        public <F> Result<T,F> mapError(Function<? super E, ? extends F> mapper) {
            return ok(value);
        }

        public <U> Result<U,E> flatMap(Function<? super T, Result<U,E>> mapper) {
            return mapper.apply(value);
        }

        public <U> U fold(Function<? super T, U> okFn, Function<? super E, U> errFn) {
            return okFn.apply(value);
        }

        public Optional<T> toOptional() {
            return Optional.of(value);
        }

        public void ifOk(Consumer<T> consumer) {
            consumer.accept(value);
        }

        public void ifError(Consumer<E> consumer) { }
    }

    record Err<T,E>(E error) implements Result<T,E> {

        public boolean isOk() { return false; }
        public boolean isError() { return true; }

        public T unwrap() {
            throw new IllegalStateException("Called unwrap on Err: " + error);
        }

        public E unwrapError() { return error; }

        public T unwrapOr(T other) { return other; }

        public <U> Result<U,E> map(Function<? super T, ? extends U> mapper) {
            return err(error);
        }

        public <F> Result<T,F> mapError(Function<? super E, ? extends F> mapper) {
            return err(mapper.apply(error));
        }

        public <U> Result<U,E> flatMap(Function<? super T, Result<U,E>> mapper) {
            return err(error);
        }

        public <U> U fold(Function<? super T, U> okFn, Function<? super E, U> errFn) {
            return errFn.apply(error);
        }

        public Optional<T> toOptional() {
            return Optional.empty();
        }

        public void ifOk(Consumer<T> consumer) { }

        public void ifError(Consumer<E> consumer) {
            consumer.accept(error);
        }
    }
}
