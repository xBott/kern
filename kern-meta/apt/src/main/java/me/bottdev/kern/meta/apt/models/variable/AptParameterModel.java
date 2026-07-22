package me.bottdev.kern.meta.apt.models.variable;

import me.bottdev.kern.meta.core.models.AnnotationModel;
import me.bottdev.kern.meta.core.models.ModelKind;
import me.bottdev.kern.meta.core.models.Modifier;
import me.bottdev.kern.meta.core.models.TypeRef;
import me.bottdev.kern.meta.core.models.variable.ParameterModel;

import java.util.List;
import java.util.Set;

record AptParameterModel(
        String simpleName,
        Set<Modifier> modifiers,
        List<AnnotationModel> annotations,
        TypeRef type
) implements ParameterModel {

    @Override public ModelKind<ParameterModel> kind() { return ModelKind.PARAMETER; }

}