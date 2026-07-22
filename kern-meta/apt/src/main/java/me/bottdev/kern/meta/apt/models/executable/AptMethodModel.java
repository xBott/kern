package me.bottdev.kern.meta.apt.models.executable;

import me.bottdev.kern.meta.core.models.*;
import me.bottdev.kern.meta.core.models.executable.MethodModel;
import me.bottdev.kern.meta.core.models.variable.ParameterModel;

import java.util.List;
import java.util.Set;

record AptMethodModel(
        String simpleName,
        Set<Modifier> modifiers,
        List<AnnotationModel> annotations,
        List<ParameterModel> parameters,
        TypeRef returnType,
        boolean isDefault
) implements MethodModel {

    @Override public ModelKind<MethodModel> kind() { return ModelKind.METHOD; }
    @Override public List<TypeParameterModel> typeParameters() { return List.of(); } // TODO
    @Override public List<TypeRef> thrownTypes() { return List.of(); }               // TODO

}