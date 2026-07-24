package me.bottdev.kern.meta.apt.configuration;

import lombok.RequiredArgsConstructor;
import me.bottdev.kern.meta.core.ProcessingContext;
import me.bottdev.kern.meta.core.configuration.AnnotationStage;
import me.bottdev.kern.meta.core.configuration.KindStage;
import me.bottdev.kern.meta.core.configuration.Pipeline;
import me.bottdev.kern.meta.core.models.ElementModel;
import me.bottdev.kern.meta.core.models.ModelKind;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class AptKindStage<M extends ElementModel> implements KindStage<M> {

    private final ProcessingContext context;
    private final List<Pipeline<?>> pipelines;
    private final ModelKind<M> kind;

    @Override
    public <A extends Annotation> AnnotationStage<M> with(Class<A> annotationType) {
        return new AptAnnotationStage<>(context, pipelines, kind, annotationType, model -> Stream.of(kind.cast(model)));
    }

}
