package me.bottdev.kern.meta.core.configuration.standalone;

import me.bottdev.kern.meta.core.MessageType;
import me.bottdev.kern.meta.core.diagnostic.DiagnosticResult;
import me.bottdev.kern.meta.core.diagnostic.bound.BoundDiagnosticBuilder;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public record ResultStandalonePipelineBuilderImpl<M>(
        List<StandalonePipeline> sink,
        Function<StandalonePipelineContext, Optional<M>> transform
) implements ResultStandalonePipelineBuilder<M> {

    @Override
    public <R> ResultStandalonePipelineBuilder<R> map(Function<M, R> mapper) {
        return next(context -> transform.apply(context).map(mapper));
    }

    @Override
    public ResultStandalonePipelineBuilder<M> filter(Predicate<M> predicate) {
        return next(context -> transform.apply(context).filter(predicate));
    }

    @Override
    public ResultStandalonePipelineBuilder<M> peek(Consumer<M> peek) {
        return next(context -> {
            Optional<M> value = transform.apply(context);
            value.ifPresent(peek);
            return value;
        });
    }

    @Override
    public ResultStandalonePipelineBuilder<M> validate(Consumer<BoundDiagnosticBuilder<M>> rules) {
        return next(context -> transform.apply(context).filter(value -> {

            BoundDiagnosticBuilder<M> diagnosticBuilder = new BoundDiagnosticBuilder<>(value);
            rules.accept(diagnosticBuilder);
            DiagnosticResult result = diagnosticBuilder.build();

            if (result.hasWarnings()) {
                result.warns().forEach(warn -> context.processing().logger().message(MessageType.WARN, warn.message()));
            }

            if (result.hasErrors()) {
                result.errors().forEach(error -> context.processing().logger().message(MessageType.ERROR, error.message()));
                return false;
            }

            return true;

        }));
    }

    @Override
    public void generate(Consumer<M> consumer) {
        sink.add(new ResultStandalonePipeline<>(transform, consumer, true));
    }

    @Override
    public void run(Consumer<M> consumer) {
        sink.add(new ResultStandalonePipeline<>(transform, consumer, false));
    }

    @Override
    public void finish() {
        run(_ -> {});
    }

    private <R> ResultStandalonePipelineBuilderImpl<R> next(Function<StandalonePipelineContext, Optional<R>> newChain) {
        return new ResultStandalonePipelineBuilderImpl<>(sink, newChain);
    }

}