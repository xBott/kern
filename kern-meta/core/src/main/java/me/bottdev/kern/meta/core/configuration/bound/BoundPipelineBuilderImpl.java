package me.bottdev.kern.meta.core.configuration.bound;

import me.bottdev.kern.meta.core.MessageType;
import me.bottdev.kern.meta.core.ProcessingContext;
import me.bottdev.kern.meta.core.diagnostic.DiagnosticResult;
import me.bottdev.kern.meta.core.diagnostic.bound.BoundDiagnosticBuilder;
import me.bottdev.kern.meta.core.models.Model;
import me.bottdev.kern.meta.core.models.ModelKind;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public record BoundPipelineBuilderImpl<M>(
        ProcessingContext context,
        List<BoundPipeline<?>> sink,
        ModelKind<?> kind,
        Class<? extends Annotation> annotationType,
        Function<Model, Stream<M>> transform
) implements BoundPipelineBuilder<M> {

    @Override
    public <R> BoundPipelineBuilder<R> map(Function<M, R> mapper) {
        return next(transform.andThen(s -> s.map(mapper)));
    }

    @Override
    public BoundPipelineBuilder<M> filter(Predicate<M> predicate) {
        return next(transform.andThen(s -> s.filter(predicate)));
    }

    @Override
    public BoundPipelineBuilder<M> validate(Consumer<BoundDiagnosticBuilder<M>> rules) {
        return next(transform.andThen(s -> s.filter(model -> {

            BoundDiagnosticBuilder<M> diagnosticBuilder = new BoundDiagnosticBuilder<>(model);
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
    public BoundPipelineBuilder<M> peek(Consumer<M> peek) {
        return next(transform.andThen(s -> s.peek(peek)));
    }

    @Override
    public void generate(Consumer<M> consumer) {
        BoundPipeline<M> pipeline = new BoundPipeline<>(kind, annotationType, transform, model -> {
            consumer.accept(model);
            return true;
        });
        sink.add(pipeline);
    }

    @Override
    public void run(Consumer<M> consumer) {
        BoundPipeline<M> pipeline = new BoundPipeline<>(kind, annotationType, transform, model -> {
            consumer.accept(model);
            return false;
        });
        sink.add(pipeline);
    }

    @Override
    public void finish() {
        run(_ -> {});
    }

    private <R> BoundPipelineBuilderImpl<R> next(Function<Model, Stream<R>> newTransform) {
        return new BoundPipelineBuilderImpl<>(context, sink, kind, annotationType, newTransform);
    }

}
