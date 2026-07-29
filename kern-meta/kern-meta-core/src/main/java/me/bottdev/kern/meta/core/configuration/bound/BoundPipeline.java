package me.bottdev.kern.meta.core.configuration.bound;

import me.bottdev.kern.meta.core.configuration.Pipeline;
import me.bottdev.kern.meta.core.models.Model;
import me.bottdev.kern.meta.core.models.ModelKind;

import java.lang.annotation.Annotation;
import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public record BoundPipeline<R>(
        ModelKind<?> kind,
        Class<? extends Annotation> annotationType,
        Function<Model, Stream<R>> transform,
        Predicate<R> predicate
) implements Pipeline<BoundPipelineContext> {

    @Override
    public boolean run(BoundPipelineContext context) {

        Model model = context.model();
        if (model.kind() != kind) return false;
        if (model.annotation(annotationType).isEmpty()) return false;

        Stream<R> stream = transform.apply(model);
        Iterator<R> iterator = stream.iterator();

        boolean continueProcessing = false;

        while (iterator.hasNext()) {
            R representation = iterator.next();
            continueProcessing |= predicate.test(representation);
        }

        return continueProcessing;

    }

}
