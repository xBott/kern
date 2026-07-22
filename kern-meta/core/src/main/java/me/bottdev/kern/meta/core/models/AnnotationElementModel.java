package me.bottdev.kern.meta.core.models;

import java.util.Optional;

public interface AnnotationElementModel extends NamedModel {

    TypeRef type();
    Optional<AnnotationValueModel> defaultValue();

}
