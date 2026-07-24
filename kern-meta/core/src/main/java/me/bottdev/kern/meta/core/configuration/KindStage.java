package me.bottdev.kern.meta.core.configuration;

import me.bottdev.kern.meta.core.models.ElementModel;

import java.lang.annotation.Annotation;

public interface KindStage<M extends ElementModel> {

    <A extends Annotation> AnnotationStage<M> with(Class<A> annotationType);

}
