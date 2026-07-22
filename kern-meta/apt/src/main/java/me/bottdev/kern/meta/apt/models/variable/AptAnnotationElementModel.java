package me.bottdev.kern.meta.apt.models.variable;

import me.bottdev.kern.meta.core.models.*;

import java.util.List;
import java.util.Optional;

public record AptAnnotationElementModel(
        String simpleName,
        List<AnnotationModel> annotations,
        TypeRef type,
        Optional<AnnotationValueModel> defaultValue
) implements AnnotationElementModel {

    @Override public ModelKind<AnnotationElementModel> kind() { return ModelKind.ANNOTATION_ELEMENT; }

}
