package me.bottdev.kern.meta.core.configuration.bound;

import me.bottdev.kern.meta.core.configuration.PipelineBuilder;
import me.bottdev.kern.meta.core.diagnostic.bound.BoundDiagnosticBuilder;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public interface BoundPipelineBuilder<M> extends PipelineBuilder {

    <R> BoundPipelineBuilder<R> map(Function<M, R> mapper);

    BoundPipelineBuilder<M> filter(Predicate<M> predicate);

    BoundPipelineBuilder<M> peek(Consumer<M> predicate);

    BoundPipelineBuilder<M> validate(Consumer<BoundDiagnosticBuilder<M>> rules);

    void finishWith(Consumer<M> consumer);

}
