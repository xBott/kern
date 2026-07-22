package me.bottdev.kern.meta.apt.configuration;

import lombok.RequiredArgsConstructor;
import me.bottdev.kern.meta.core.configuration.AnnotationStage;
import me.bottdev.kern.meta.core.configuration.KindStage;
import me.bottdev.kern.meta.core.configuration.Pipeline;
import me.bottdev.kern.meta.core.models.Model;
import me.bottdev.kern.meta.core.models.ModelKind;

import java.lang.annotation.Annotation;
import java.util.List;

@RequiredArgsConstructor
public class AptKindStage<M extends Model> implements KindStage<M> {

    private final List<Pipeline<?, ?>> pipelines;
    private final ModelKind<M> kind;

    @Override
    public <A extends Annotation> AnnotationStage<M, A> with(Class<A> annotationType) {
        return new AptAnnotationStage<>(pipelines, kind, annotationType);
    }


}
