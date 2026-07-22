package me.bottdev.kern.meta.core.configuration;

import me.bottdev.kern.meta.core.models.Model;

import java.lang.annotation.Annotation;
import java.util.function.BiConsumer;

public interface AnnotationStage<M extends Model, A extends Annotation> {

    void peek(BiConsumer<M, A> consumer);

}
