package me.bottdev.kern.meta.core.configuration;

import me.bottdev.kern.meta.core.models.Model;

import java.lang.annotation.Annotation;

public interface KindStage<M extends Model> {

    <A extends Annotation> AnnotationStage<M, A> with(Class<A> annotationType);

}
