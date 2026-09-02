package me.bottdev.kern.meta.apt.models.variable;

import me.bottdev.kern.meta.core.models.*;
import me.bottdev.kern.meta.core.models.variable.FieldModel;

import java.util.List;
import java.util.Optional;
import java.util.Set;

record AptFieldModel(
        ElementHandle handle,
        String simpleName,
        Set<Modifier> modifiers,
        List<AnnotationModel> annotations,
        TypeRef type,
        Optional<Object> constantValue
) implements FieldModel {

    @Override public ModelKind<FieldModel> kind() { return ModelKind.FIELD; }

}