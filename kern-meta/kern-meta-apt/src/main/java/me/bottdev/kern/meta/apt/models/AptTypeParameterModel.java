package me.bottdev.kern.meta.apt.models;

import me.bottdev.kern.meta.core.models.*;

import java.util.List;

public record AptTypeParameterModel(
        ElementHandle handle,
        String simpleName,
        List<AnnotationModel> annotations,
        List<TypeRef> bounds
) implements TypeParameterModel {

    @Override public ModelKind<TypeParameterModel> kind() { return ModelKind.TYPE_PARAMETER; }

}