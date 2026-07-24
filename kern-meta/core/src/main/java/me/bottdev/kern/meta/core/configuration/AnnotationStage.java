package me.bottdev.kern.meta.core.configuration;

import me.bottdev.kern.meta.core.diagnostic.builders.ModelDiagnosticBuilder;
import me.bottdev.kern.meta.core.models.Model;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public interface AnnotationStage<M extends Model> {

    <R extends Model> AnnotationStage<R> map(Function<M, R> mapper);

    AnnotationStage<M> filter(Predicate<M> predicate);

    AnnotationStage<M> validate(Consumer<ModelDiagnosticBuilder<M>> rules);

    AnnotationStage<M> peek(Consumer<M> peek);

    void accept(Consumer<M> peek);

    default void accept() {
        accept(_ -> {});
    }

}
