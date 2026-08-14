package me.bottdev.kern.commons.wrapper;

import me.bottdev.kern.commons.diagnostic.Diagnostic;
import me.bottdev.kern.commons.diagnostic.DiagnosticException;
import me.bottdev.kern.commons.diagnostic.DiagnosticType;
import me.bottdev.kern.commons.diagnostic.Diagnostics;
import org.jspecify.annotations.NonNull;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/// Interface for a result with diagnostics.
/// There are 2 types of result:
/// - [Success]: it means that the value is present and diagnostics are empty or do not contain any errors.
/// - [Failure]: it means that the value is absent (null).
/// Diagnostics may can be absent too, but usually they are present and contain some errors.
///
/// Use factory methods to create a [DiagnosticResult] wrapper:
/// - [DiagnosticResult#success(Object)] to return a wrapper with a present value, but without diagnostics.
/// - [DiagnosticResult#success(Object, Diagnostics)] to return a wrapper with a present value and diagnostics without errors.
/// - [DiagnosticResult#failure(Diagnostics)] to return a wrapper with diagnostics, but with an absent value.
///
/// @param <T> Wrapped object.
/// @param <D> Type of diagnostic.
public sealed interface DiagnosticResult<T, D extends Diagnostic> extends ObjectWrapper<T> permits
    DiagnosticResult.Success,
    DiagnosticResult.Failure
{

    /// @return Indicates whether the wrapper holds any diagnostics.
    boolean hasDiagnostics();
    /// @return Indicates whether the wrapper holds diagnostics of a specified type.
    boolean hasDiagnostics(DiagnosticType type);

    /// @return [Diagnostics] or null
    Diagnostics<D> unwrapDiagnostics();

    /// Executes a provided function that takes [Diagnostics] as an argument if they are present.
    void ifDiagnosticsPresent(Consumer<Diagnostics<D>> consumer);
    /// Executes a provided function that takes [T] and [Diagnostics] as arguments if diagnostics are present.
    void ifDiagnosticsPresent(BiConsumer<T, Diagnostics<D>> consumer);

    /// Executes a provided function that takes [Diagnostics] as an argument if diagnostics of
    /// specified type are present.
    void ifDiagnosticsPresent(DiagnosticType type, Consumer<Diagnostics<D>> consumer);
    /// Executes a provided function that takes [T] and [Diagnostics] as arguments if diagnostics of
    /// specified type are present.
    void ifDiagnosticsPresent(DiagnosticType type, BiConsumer<T, Diagnostics<D>> consumer);

    /// Folds the result into a single value by applying one of two functions depending on the state.
    ///
    /// @param presentFn  The function to apply if the result is present.
    /// @param errFn The function to apply if result contains errors.
    /// @param <U>   The return type.
    /// @return The result of applying the respective function.
    <U> U fold(Function<T, U> presentFn, Function<Diagnostics<D>, U> errFn);

    /// Unwraps the value or throws an exception if it is absent.
    /// @throws DiagnosticException if wrapped value is absent.
    /// @return [T] if value is present.
    default T unwrapOrThrow() throws DiagnosticException {
        if (this instanceof Success<T, D> success) return success.unwrap();
        Failure<T, D> failure = (Failure<T, D>) this;
        throw new DiagnosticException(failure.diagnostics());
    }

    /// @return [Success] diagnostic result wrapper with provided value and no diagnostics.
    static <T, D extends Diagnostic> DiagnosticResult<T, D> success(@NonNull T value) {
        return new Success<>(value, null);
    }

    /// @throws IllegalArgumentException if diagnostics contain any errors.
    /// @return [Success] diagnostic result wrapper with provided value and diagnostics.
    static <T, D extends Diagnostic> DiagnosticResult<T, D> success(@NonNull T value, Diagnostics<D> diagnostics) {
        if (diagnostics.has(DiagnosticType.ERROR))
            throw new IllegalArgumentException("Diagnostics cannot contain errors in success diagnostic result.");
        return new Success<>(value, diagnostics);
    }

    /// @return [Failure] diagnostic result wrapper with provided diagnostics.
    static <T, D extends Diagnostic> DiagnosticResult<T, D> failure(Diagnostics<D> diagnostics) {
        return new Failure<>(diagnostics);
    }

    /// Success implementation of [DiagnosticResult].
    /// Diagnostics do not contain any errors.
    record Success<T, D extends Diagnostic>(
            @NonNull T value,
            Diagnostics<D> diagnostics
    ) implements DiagnosticResult<T, D> {

        @Override
        public boolean hasDiagnostics() {
            return diagnostics != null && !diagnostics.isEmpty();
        }

        @Override
        public boolean hasDiagnostics(DiagnosticType type) {
            return diagnostics != null && diagnostics.has(type);
        }

        @Override
        public boolean isPresent() {
            return true;
        }

        @Override
        public T unwrap() {
            return value;
        }

        @Override
        public T unwrapOr(T other) {
            return value;
        }

        @Override
        public Diagnostics<D> unwrapDiagnostics() {
            return diagnostics;
        }

        @Override
        public <U> DiagnosticResult<U, D> map(Function<T, U> mapper) {
            return new Success<>(mapper.apply(value), diagnostics);
        }

        @Override
        public <U, W extends ObjectWrapper<U>> W flatMap(Function<T, W> mapper) {
            return mapper.apply(value);
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
        public void ifDiagnosticsPresent(Consumer<Diagnostics<D>> consumer) {
            if (hasDiagnostics()) consumer.accept(diagnostics);
        }

        @Override
        public void ifDiagnosticsPresent(BiConsumer<T, Diagnostics<D>> consumer) {
            if (hasDiagnostics()) consumer.accept(value, diagnostics);
        }

        @Override
        public void ifDiagnosticsPresent(DiagnosticType type, Consumer<Diagnostics<D>> consumer) {
            if (hasDiagnostics(type)) consumer.accept(diagnostics);
        }

        @Override
        public void ifDiagnosticsPresent(DiagnosticType type, BiConsumer<T, Diagnostics<D>> consumer) {
            if (hasDiagnostics(type)) consumer.accept(value, diagnostics);
        }

        @Override
        public <U> U fold(Function<T, U> presentFn, Function<Diagnostics<D>, U> errFn) {
            return presentFn.apply(value);
        }

    }

    /// Failure implementation of [DiagnosticResult].
    /// Does not contain a value.
    /// May contain [Diagnostics], but not necessary.
    record Failure<T, D extends Diagnostic>(
            Diagnostics<D> diagnostics
    ) implements DiagnosticResult<T, D> {

        @Override
        public boolean hasDiagnostics() {
            return diagnostics != null && !diagnostics.isEmpty();
        }

        @Override
        public boolean hasDiagnostics(DiagnosticType type) {
            return diagnostics != null && diagnostics.has(type);
        }

        @Override
        public Diagnostics<D> unwrapDiagnostics() {
            return diagnostics;
        }

        @Override
        public boolean isPresent() {
            return false;
        }

        @Override
        public T unwrap() {
            throw new IllegalStateException("Cannot unwrap value from a failed DiagnosticResult. Diagnostics: " + diagnostics);
        }

        @Override
        public T unwrapOr(T other) {
            return other;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <U> DiagnosticResult<U, D> map(Function<T, U> mapper) {
            return (DiagnosticResult<U, D>) this;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <U, W extends ObjectWrapper<U>> W flatMap(Function<T, W> mapper) {
            return (W) this;
        }

        @Override
        public Optional<T> toOptional() {
            return Optional.empty();
        }

        @Override
        public void ifPresent(Consumer<T> consumer) {

        }

        @Override
        public void ifDiagnosticsPresent(Consumer<Diagnostics<D>> consumer) {
            if (hasDiagnostics()) consumer.accept(diagnostics);
        }

        @Override
        public void ifDiagnosticsPresent(BiConsumer<T, Diagnostics<D>> consumer) {

        }

        @Override
        public void ifDiagnosticsPresent(DiagnosticType type, Consumer<Diagnostics<D>> consumer) {
            if (hasDiagnostics(type)) consumer.accept(diagnostics);
        }

        @Override
        public void ifDiagnosticsPresent(DiagnosticType type, BiConsumer<T, Diagnostics<D>> consumer) {

        }

        @Override
        public <U> U fold(Function<T, U> presentFn, Function<Diagnostics<D>, U> errFn) {
            return errFn.apply(diagnostics);
        }
    }



}
