package me.bottdev.kern.meta.apt.configuration;

import lombok.RequiredArgsConstructor;
import me.bottdev.kern.meta.core.MessageType;
import me.bottdev.kern.meta.core.ProcessingContext;
import me.bottdev.kern.meta.core.configuration.AnnotationStage;
import me.bottdev.kern.meta.core.configuration.Pipeline;
import me.bottdev.kern.meta.core.diagnostic.builders.ModelDiagnosticBuilder;
import me.bottdev.kern.meta.core.diagnostic.DiagnosticResult;
import me.bottdev.kern.meta.core.models.ElementModel;
import me.bottdev.kern.meta.core.models.Model;
import me.bottdev.kern.meta.core.models.ModelKind;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
@RequiredArgsConstructor
public class AptAnnotationStage<M extends Model> implements AnnotationStage<M> {

    private final ProcessingContext context;
    private final List<Pipeline<?>> pipelines;
    private final ModelKind<?> kind;
    private final Class<? extends Annotation> annotationType;
    private final Function<ElementModel, Stream<M>> transform;

    @Override
    public <R extends Model> AnnotationStage<R> map(Function<M, R> mapper) {
        return next(transform.andThen(s -> s.map(mapper)));
    }

    @Override
    public AnnotationStage<M> filter(Predicate<M> predicate) {
        return next(transform.andThen(s -> s.filter(predicate)));
    }

    @Override
    public AnnotationStage<M> validate(Consumer<ModelDiagnosticBuilder<M>> rules) {
        return next(transform.andThen(s -> s.filter(model -> {

            ModelDiagnosticBuilder<M> diagnosticBuilder = new ModelDiagnosticBuilder<>(model);
            rules.accept(diagnosticBuilder);
            DiagnosticResult result = diagnosticBuilder.build();

            if (result.hasWarnings()) {
                result.warns().forEach(warn -> context.logger().message(MessageType.WARN, warn.message(), model));
            }

            if (result.hasErrors()) {
                result.errors().forEach(error -> context.logger().message(MessageType.ERROR, error.message(), model));
            }

            return !result.hasErrors();

        })));
    }

    @Override
    public AnnotationStage<M> peek(Consumer<M> peek) {
        return next(transform.andThen(s -> s.peek(peek)));
    }

    @Override
    public void accept(Consumer<M> peek) {
        Pipeline<M> pipeline = new Pipeline<>(kind, annotationType, transform, peek);
        pipelines.add(pipeline);
    }

    @SuppressWarnings("unchecked")
    private <R extends Model> AptAnnotationStage<R> next(Function<ElementModel, Stream<R>> newTransform) {
        return new AptAnnotationStage<>(context, pipelines, kind, annotationType, newTransform);
    }

}

