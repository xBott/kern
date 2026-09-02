package me.bottdev.kern.meta.apt.models.variable;

import me.bottdev.kern.meta.core.models.*;
import me.bottdev.kern.meta.core.models.variable.ParameterModel;

import java.util.List;
import java.util.Set;

record AptParameterModel(
        ElementHandle handle,
        String simpleName,
        Set<Modifier> modifiers,
        List<AnnotationModel> annotations,
        TypeRef type
) implements ParameterModel {

    @Override public ModelKind<ParameterModel> kind() { return ModelKind.PARAMETER; }

}