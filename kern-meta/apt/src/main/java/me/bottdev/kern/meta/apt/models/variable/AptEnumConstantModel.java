package me.bottdev.kern.meta.apt.models.variable;

import me.bottdev.kern.meta.core.models.*;
import me.bottdev.kern.meta.core.models.variable.EnumConstantModel;

import java.util.List;
import java.util.Set;

public record AptEnumConstantModel(
        ElementHandle handle,
        String simpleName,
        Set<Modifier> modifiers,
        List<AnnotationModel> annotations,
        TypeRef type
) implements EnumConstantModel {

    @Override public ModelKind<EnumConstantModel> kind() { return ModelKind.ENUM_CONSTANT; }

}