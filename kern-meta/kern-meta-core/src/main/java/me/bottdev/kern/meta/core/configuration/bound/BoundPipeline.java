package me.bottdev.kern.meta.core.configuration.bound;

import me.bottdev.kern.meta.core.configuration.Pipeline;
import me.bottdev.kern.meta.core.models.Model;
import me.bottdev.kern.meta.core.models.ModelKind;

import java.lang.annotation.Annotation;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public record BoundPipeline<M>(
        ModelKind<?> kind,
        Class<? extends Annotation> annotationType,
        Function<Model, Stream<M>> transform,
        Consumer<M> consumer
) implements Pipeline<BoundPipelineContext> {

    @Override
    public void run(BoundPipelineContext context) {

        Model model = context.model();
        if (model.kind() != kind) return;
        if (model.annotation(annotationType).isEmpty()) return;

        transform.apply(model).forEach(consumer);

    }

}
