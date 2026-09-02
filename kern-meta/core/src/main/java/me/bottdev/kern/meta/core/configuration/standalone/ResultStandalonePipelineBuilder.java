package me.bottdev.kern.meta.core.configuration.standalone;

import me.bottdev.kern.meta.core.configuration.PipelineBuilder;
import me.bottdev.kern.meta.core.diagnostic.bound.BoundDiagnosticBuilder;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public interface ResultStandalonePipelineBuilder<M> extends PipelineBuilder {

    <R> ResultStandalonePipelineBuilder<R> map(Function<M, R> mapper);

    ResultStandalonePipelineBuilder<M> filter(Predicate<M> predicate);

    ResultStandalonePipelineBuilder<M> peek(Consumer<M> peek);

    ResultStandalonePipelineBuilder<M> validate(Consumer<BoundDiagnosticBuilder<M>> rules);

    void generate(Consumer<M> consumer);

    void run(Consumer<M> consumer);

}