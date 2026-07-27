package me.bottdev.kern.meta.core.models;

import java.util.Map;

public interface AnnotationModel {

    TypeRef type();
    Map<String, AnnotationValueModel> values();

}
