package me.bottdev.kern.meta.apt.models.executable;

import me.bottdev.kern.meta.core.models.*;
import me.bottdev.kern.meta.core.models.executable.ConstructorModel;
import me.bottdev.kern.meta.core.models.variable.ParameterModel;

import java.util.List;
import java.util.Set;

record AptConstructorModel(
        String simpleName,
        Set<Modifier> modifiers,
        List<AnnotationModel> annotations,
        List<ParameterModel> parameters
) implements ConstructorModel {

    @Override public ModelKind<ConstructorModel> kind() { return ModelKind.CONSTRUCTOR; }
    @Override public List<TypeParameterModel> typeParameters() { return List.of(); }        // TODO
    @Override public List<TypeRef> thrownTypes() { return List.of(); }                      // TODO

}