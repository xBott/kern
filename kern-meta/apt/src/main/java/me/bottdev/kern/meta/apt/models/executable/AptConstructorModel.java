package me.bottdev.kern.meta.apt.models.executable;

import me.bottdev.kern.meta.core.models.*;
import me.bottdev.kern.meta.core.models.executable.ConstructorModel;
import me.bottdev.kern.meta.core.models.variable.ParameterModel;

import java.util.List;
import java.util.Set;

record AptConstructorModel(
        ElementHandle handle,
        String simpleName,
        Set<Modifier> modifiers,
        List<AnnotationModel> annotations,
        List<ParameterModel> parameters,
        List<TypeParameterModel> typeParameters,
        List<TypeRef> thrownTypes
) implements ConstructorModel {

    @Override public ModelKind<ConstructorModel> kind() { return ModelKind.CONSTRUCTOR; }

}