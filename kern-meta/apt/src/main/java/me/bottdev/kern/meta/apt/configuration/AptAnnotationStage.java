package me.bottdev.kern.meta.apt.configuration;

import lombok.RequiredArgsConstructor;
import me.bottdev.kern.meta.core.configuration.AnnotationStage;
import me.bottdev.kern.meta.core.configuration.Pipeline;
import me.bottdev.kern.meta.core.models.Model;
import me.bottdev.kern.meta.core.models.ModelKind;

import java.lang.annotation.Annotation;
import java.util.List;import java.util.function.BiConsumer;

@RequiredArgsConstructor
public class AptAnnotationStage<M extends Model, A extends Annotation> implements AnnotationStage<M, A> {

    private final List<Pipeline<?, ?>> pipelines;
    private final ModelKind<M> kind;
    private final Class<A> annotationType;

    @Override
    public void peek(BiConsumer<M, A> peek) {
        Pipeline<M, A> pipeline = new Pipeline<>(kind, annotationType, peek);
        pipelines.add(pipeline);
    }

}
