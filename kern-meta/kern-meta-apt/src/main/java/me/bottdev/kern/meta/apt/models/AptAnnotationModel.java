package me.bottdev.kern.meta.apt.models;

import me.bottdev.kern.meta.core.models.AnnotationModel;
import me.bottdev.kern.meta.core.models.AnnotationValueModel;
import me.bottdev.kern.meta.core.models.TypeRef;

import java.util.Map;

public record AptAnnotationModel(
        TypeRef type,
        Map<String, AnnotationValueModel> values
) implements AnnotationModel {



}
