package me.bottdev.kern.meta.core.configuration;

import me.bottdev.kern.meta.core.ProcessingContext;
import me.bottdev.kern.meta.core.models.ElementModel;
import me.bottdev.kern.meta.core.models.ModelKind;

import java.lang.annotation.Annotation;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public record Pipeline<M>(
        ModelKind<?> kind,
        Class<? extends Annotation> annotationType,
        Function<ElementModel, Stream<M>> transform,
        Consumer<M> acceptConsumer
) {

    public void run(ElementModel model, ProcessingContext context) {
        if (model.kind() != kind) return;
        if (model.annotation(annotationType).isEmpty()) return;
        transform.apply(model).forEach(acceptConsumer);

    }

}
