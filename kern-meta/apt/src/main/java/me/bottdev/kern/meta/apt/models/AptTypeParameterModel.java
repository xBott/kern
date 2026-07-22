package me.bottdev.kern.meta.apt.models;

import me.bottdev.kern.meta.core.models.AnnotationModel;
import me.bottdev.kern.meta.core.models.ModelKind;
import me.bottdev.kern.meta.core.models.TypeParameterModel;
import me.bottdev.kern.meta.core.models.TypeRef;

import java.util.List;

public record AptTypeParameterModel(
        String simpleName,
        List<AnnotationModel> annotations,
        List<TypeRef> bounds
) implements TypeParameterModel {

    @Override public ModelKind<TypeParameterModel> kind() { return ModelKind.TYPE_PARAMETER; }

}